package io.github.zodh.video.infrastructure.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.github.zodh.video.application.gateway.EmailSenderGateway;
import io.github.zodh.video.application.gateway.VideoFileManagerGateway;
import io.github.zodh.video.application.model.upload.GatewayUploadResponse;
import io.github.zodh.video.domain.model.video.VideoCutter;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import io.github.zodh.video.infrastructure.adapters.dto.VideoStatusUpdateMessage;
import io.github.zodh.video.infrastructure.adapters.exception.InvalidStatusUpdateMessage;
import io.github.zodh.video.infrastructure.configuration.AwsVideoServiceConfig;
import io.github.zodh.video.infrastructure.database.repository.VideoCutterJpaRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.sqs.model.Message;

@Slf4j
@Component
public class VideoFileManagerAWSAdapter implements VideoFileManagerGateway {

  @Value("${video.bucket-name}")
  private String bucketName;
  @Value("${video.upload.link.duration-in-minutes}")
  private String uploadLinkDurationInMinutes;
  @Value("${upload.expiration.time-in-minutes}")
  private int uploadExpirationTime;
  private final S3Presigner s3Presigner;
  private final VideoCutterJpaRepository videoCutterJpaRepository;
  private final ObjectMapper objectMapper;
  private final EmailSenderGateway emailSenderGateway;

  @Autowired
  public VideoFileManagerAWSAdapter(AwsVideoServiceConfig s3Config, VideoCutterJpaRepository videoCutterJpaRepository, ObjectMapper objectMapper, EmailSenderSpringAdapter emailSenderGateway) {
    this.s3Presigner = s3Config.getPreSigner();
    this.videoCutterJpaRepository = videoCutterJpaRepository;
    this.objectMapper = objectMapper;
    this.emailSenderGateway = emailSenderGateway;
  }

  @Override
  public GatewayUploadResponse generateUploadUrl(VideoCutter videoCutter, MultipartFile multipartFile) {
    String fileId = String.valueOf(videoCutter.getCutIntervalInSeconds())
        .concat("-")
        .concat(UUID.randomUUID().toString())
        .concat("-")
        .concat(videoCutter.getVideo().getName());
    Duration d = maxUploadDuration();
    PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(d)
        .putObjectRequest(ur -> ur.bucket(bucketName).key(fileId).contentType(multipartFile.getContentType()))
        .build();
    PresignedPutObjectRequest preSignedUploadRequest = s3Presigner.presignPutObject(preSignRequest);
    return new GatewayUploadResponse(
        preSignedUploadRequest.url().toString(),
        preSignedUploadRequest.httpRequest().method().name(),
        fileId,
        String.format("%s minutes", uploadLinkDurationInMinutes)
    );
  }

  @Transactional
  @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
  @Override
  public void invalidateNotUploadedVideos() {
    List<Long> videosToInvalidate = videoCutterJpaRepository.fetchVideosNotUploadedInMinutes(LocalDateTime.now().minusMinutes(uploadExpirationTime));
    if (!CollectionUtils.isEmpty(videosToInvalidate)) {
      videoCutterJpaRepository.invalidateVideosToUpload(videosToInvalidate, LocalDateTime.now());
    }
  }

  @Transactional
  @SqsListener("${video.status.update.queue-name}")
  public void receiveStatusUpdate(Message queueMessage) {
    try {
      VideoStatusUpdateMessage videoStatusUpdateMessageByUpload = parseDefaultUploadMessage(queueMessage.body());
      VideoStatusUpdateMessage videoStatusUpdateMessageByPublisher = parsePublishedMessage(queueMessage.body());
      VideoStatusUpdateMessage videoStatusUpdateMessage = Stream.of(videoStatusUpdateMessageByUpload, videoStatusUpdateMessageByPublisher).filter(vsu -> Objects.nonNull(vsu) && StringUtils.isNotBlank(vsu.fileId())).findFirst().orElseThrow(InvalidStatusUpdateMessage::new);
      updateVideoProcessingStatus(videoStatusUpdateMessage.fileId(), videoStatusUpdateMessage.status(), videoStatusUpdateMessage.url());
      log.info("Video {} updated to status {} successfully!", videoStatusUpdateMessage.fileId(), videoStatusUpdateMessage.status());
    } catch (Exception e) {
      log.error("Error trying to process file update message! Message: " + e.getMessage(), e);
    }
  }

  private VideoStatusUpdateMessage parseDefaultUploadMessage(String msg) {
    try {
      S3EventNotification eventNotification = S3EventNotification.fromJson(new JSONObject(msg).getString("Message"));
      return new VideoStatusUpdateMessage(
          eventNotification.getRecords().stream()
              .findFirst()
              .orElseThrow(InvalidStatusUpdateMessage::new)
              .getS3()
              .getObject()
              .getKey(),
          VideoProcessingStatusEnum.AWAITING_PROCESSING,
          null
      );
    } catch (Exception e) {
      return null;
    }
  }

  private VideoStatusUpdateMessage parsePublishedMessage(String message) {
    try {
      return this.objectMapper.readValue(message, VideoStatusUpdateMessage.class);
    } catch (Exception e) {
      return null;
    }
  }


  @Override
  public void updateVideoProcessingStatus(String fileId, VideoProcessingStatusEnum status, String url) {
    if (status == VideoProcessingStatusEnum.FINISHED && StringUtils.isNotBlank(url)) {
      videoCutterJpaRepository.updateVideoCutterUrl(url, fileId, LocalDateTime.now());
    }
    videoCutterJpaRepository.updateVideoCutterProcessingStatus(fileId, status, LocalDateTime.now());
    if (status.equals(VideoProcessingStatusEnum.FINISHED) || status.equals(VideoProcessingStatusEnum.ERROR)) {
      var videoCutterEntity = videoCutterJpaRepository.findByFileId(fileId).orElseThrow(() -> new RuntimeException("Video not found!"));
      String videoName = videoCutterEntity.getName();
      String optLink = status.equals(VideoProcessingStatusEnum.FINISHED) ? String.format("Link para download: %s", url) : "";
      emailSenderGateway.send(
          "Processamento de frames",
          """
              Olá!
                                \s
              Somos a Zodh Video e estamos passando aqui pra informar que o status do vídeo '%s' foi atualizado!
                                \s
              Status atual: %s
                                \s
              %s
             \s""".formatted(videoName, status.getDescription(), optLink),
          videoCutterEntity.getUserEmail()
      );
    }
  }

  private Duration maxUploadDuration() {
    return Duration.ofMinutes(Long.parseLong(this.uploadLinkDurationInMinutes));
  }

}
