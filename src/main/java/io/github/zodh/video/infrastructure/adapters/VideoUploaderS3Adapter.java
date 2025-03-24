package io.github.zodh.video.infrastructure.adapters;

import io.github.zodh.video.application.gateway.VideoUploaderGateway;
import io.github.zodh.video.application.model.upload.GatewayUploadResponse;
import io.github.zodh.video.domain.model.video.VideoCutter;
import io.github.zodh.video.infrastructure.configuration.AwsS3Config;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
public class VideoUploaderS3Adapter implements VideoUploaderGateway {

  @Value("${video.bucket-name}")
  private String bucketName;
  @Value("${video.max-upload.duration-in-minutes}")
  private String maxUploadDuration;
  private final S3Presigner s3Presigner;

  @Autowired
  public VideoUploaderS3Adapter(AwsS3Config s3Config) {
    this.s3Presigner = s3Config.getPreSigner();
  }

  @Override
  public GatewayUploadResponse generateUploadUrl(VideoCutter videoCutter, MultipartFile multipartFile) {
    String fileId = UUID.randomUUID().toString().concat("-").concat(videoCutter.getVideo().getName());
    PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(maxUploadDuration())
        .putObjectRequest(ur -> ur.bucket(bucketName).key(fileId).contentType(multipartFile.getContentType()))
        .build();
    PresignedPutObjectRequest preSignedUploadRequest = s3Presigner.presignPutObject(preSignRequest);
    return new GatewayUploadResponse(
        preSignedUploadRequest.url().toString(),
        preSignedUploadRequest.httpRequest().method().name(),
        fileId,
        String.format("%s minutes", maxUploadDuration)
    );
  }

  private Duration maxUploadDuration() {
    return Duration.ofMinutes(Long.parseLong(this.maxUploadDuration));
  }

}
