package io.github.zodh.video.infrastructure.adapters;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.github.zodh.video.application.gateway.VideoFileManagerGateway;
import io.github.zodh.video.application.model.upload.GatewayUploadResponse;
import io.github.zodh.video.domain.model.video.VideoCutter;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import io.github.zodh.video.infrastructure.configuration.AwsVideoServiceConfig;
import io.github.zodh.video.infrastructure.database.repository.VideoCutterJpaRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
public class VideoFileManagerS3Adapter implements VideoFileManagerGateway {

  @Value("${video.bucket-name}")
  private String bucketName;
  @Value("${video.upload.link.duration-in-minutes}")
  private String uploadLinkDurationInMinutes;
  @Value("${upload.expiration.time-in-minutes}")
  private int uploadExpirationTime;
  private final S3Presigner s3Presigner;
  private final VideoCutterJpaRepository videoCutterJpaRepository;

  @Autowired
  public VideoFileManagerS3Adapter(AwsVideoServiceConfig s3Config, VideoCutterJpaRepository videoCutterJpaRepository) {
    this.s3Presigner = s3Config.getPreSigner();
    this.videoCutterJpaRepository = videoCutterJpaRepository;
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
      videoCutterJpaRepository.invalidateVideosToUpload(videosToInvalidate);
    }
  }

  @Transactional
  @SqsListener("${video.status.update.queue-name}")
  public void receiveStatusUpdate(Message<String> queueMessage) {
    try {
      JSONObject messageAsJson = new JSONObject(queueMessage.getPayload());
      JSONObject payloadAsJson = new JSONObject(messageAsJson.getString("Message"));
      String fileId = payloadAsJson.getJSONArray("Records")
          .getJSONObject(0)
          .getJSONObject("s3")
          .getJSONObject("object")
          .getString("key");
      videoCutterJpaRepository.updateVideoCutterProcessingStatus(fileId, VideoProcessingStatusEnum.AWAITING_PROCESSING);
    } catch (Exception e) {
      log.error("Error trying to process file update message!");
    }
  }


  @Override
  public void updateUploadedVideoStatus(String fileId) {
    videoCutterJpaRepository.updateVideoCutterProcessingStatus(fileId, VideoProcessingStatusEnum.AWAITING_PROCESSING);
  }

  private Duration maxUploadDuration() {
    return Duration.ofMinutes(Long.parseLong(this.uploadLinkDurationInMinutes));
  }

}
