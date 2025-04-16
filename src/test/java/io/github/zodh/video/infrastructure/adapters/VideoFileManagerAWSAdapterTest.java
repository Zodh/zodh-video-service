package io.github.zodh.video.infrastructure.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.zodh.video.application.model.upload.GatewayUploadResponse;
import io.github.zodh.video.domain.model.user.User;
import io.github.zodh.video.domain.model.video.Video;
import io.github.zodh.video.domain.model.video.VideoCutter;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import io.github.zodh.video.infrastructure.configuration.AwsVideoServiceConfig;
import io.github.zodh.video.infrastructure.database.repository.VideoCutterJpaRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.sqs.model.Message;

@ExtendWith(MockitoExtension.class)
class VideoFileManagerAWSAdapterTest {

  @InjectMocks
  private VideoFileManagerAWSAdapter videoFileManagerAWSAdapter;
  @Mock
  private S3Presigner s3Presigner;
  @Mock
  private VideoCutterJpaRepository videoCutterJpaRepository;
  @Mock
  private AwsVideoServiceConfig awsVideoServiceConfig;
  @Mock
  private EmailSenderSpringAdapter emailSenderSpringAdapter;
  @Spy
  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    lenient().when(awsVideoServiceConfig.getPreSigner()).thenReturn(s3Presigner);
    this.videoFileManagerAWSAdapter = new VideoFileManagerAWSAdapter(awsVideoServiceConfig, videoCutterJpaRepository, objectMapper, emailSenderSpringAdapter);
    ReflectionTestUtils.setField(videoFileManagerAWSAdapter, "bucketName", "raw-videos");
    ReflectionTestUtils.setField(videoFileManagerAWSAdapter, "uploadLinkDurationInMinutes", "5");
    ReflectionTestUtils.setField(videoFileManagerAWSAdapter, "uploadExpirationTime", 5);
  }

  @Test
  @DisplayName("Given valid video cutter and multipart file "
      + "When generate upload url "
      + "Then use external dependencies to generate url successfully")
  void generateUploadUrlSuccessfully() {
    LocalDateTime sampleLdt = LocalDateTime.of(2025, Month.MARCH, 29, 10, 54);
    UUID userId = UUID.randomUUID();
    User user = new User("test@test.com", userId);
    Video video = new Video( "example-video.mp4", "mp4", 5120L, user, sampleLdt, sampleLdt, VideoProcessingStatusEnum.FINISHED);
    VideoCutter videoCutter = new VideoCutter(video, 5);
    MultipartFile multipartFile = new MockMultipartFile("example-video.mp4", new byte[5120]);
    when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
        .thenReturn(PresignedPutObjectRequest.builder().expiration(Instant.now()).isBrowserExecutable(true).signedHeaders(Map.of("test", List.of("test"))).httpRequest(SdkHttpRequest.builder().protocol("http").host("localhost").method(SdkHttpMethod.PUT).build()).build());
    GatewayUploadResponse response = videoFileManagerAWSAdapter.generateUploadUrl(videoCutter, multipartFile);
    assertThat(response).isNotNull();
    assertThat(response.url()).isEqualTo("http://localhost");
    assertThat(response.method()).isEqualTo("PUT");
    assertThat(response.fileId()).isNotEmpty();
    assertThat(response.duration()).isEqualTo("5 minutes");
  }

  @Test
  @DisplayName("Given some videos to invalidate "
      + "When videos to invalidate is not empty "
      + "Then invalidate returned videos")
  void invalidateVideosSuccessfully() {
    when(videoCutterJpaRepository.fetchVideosNotUploadedInMinutes(any()))
        .thenReturn(List.of(1L, 2L, 3L));
    videoFileManagerAWSAdapter.invalidateNotUploadedVideos();
    verify(videoCutterJpaRepository, times(1)).invalidateVideosToUpload(eq(List.of(1L, 2L, 3L)), any(LocalDateTime.class));
  }

  @Test
  @DisplayName("Given empty videos to invalidate "
      + "When invalidate verify if must invalidate any video "
      + "Then do nothing")
  void noVideosToInvalidateInScheduler() {
    when(videoCutterJpaRepository.fetchVideosNotUploadedInMinutes(any()))
        .thenReturn(List.of());
    videoFileManagerAWSAdapter.invalidateNotUploadedVideos();
    verifyNoMoreInteractions(videoCutterJpaRepository);
  }

  @Test
  @DisplayName("Given queue status message "
      + "When message is from sns "
      + "Then parse default message "
      + "and update video status successfully")
  void defaultMessageVideoStatusUpdateSuccess() {
    Message sample = Message.builder().body(
        """
            {
                "Message": {
                    "Records": [
                        {
                            "s3": {
                                "object": {
                                    "key": "file-key"
                                }
                            }
                        }
                    ]
                }
            } 
           """
    ).build();
    videoFileManagerAWSAdapter.receiveStatusUpdate(sample);
    verify(videoCutterJpaRepository, times(1)).updateVideoCutterProcessingStatus(eq("file-key"), eq(VideoProcessingStatusEnum.AWAITING_PROCESSING), any(LocalDateTime.class));
  }

  @Test
  @DisplayName("Given queue status message "
      + "When message is from any client "
      + "Then parse published message "
      + "and update video status successfully")
  void publishedMessageVideoStatusUpdateSuccess() {
    Message sample = Message.builder().body(
        """
            {
                 "fileId": "file-key",
                 "status": "PROCESSING",
                 "url": null
             }
           """
    ).build();
    videoFileManagerAWSAdapter.receiveStatusUpdate(sample);
    verify(videoCutterJpaRepository, times(1)).updateVideoCutterProcessingStatus(eq("file-key"), eq(VideoProcessingStatusEnum.PROCESSING), any(LocalDateTime.class));
  }

  @Test
  @DisplayName("Given invalid message "
      + "When try to parse "
      + "Then throws and handle exception")
  void failureTryingToUpdateVideoStatus() {
    Message sample = Message.builder().body(
        """
            {
                 "invalidClaim": "test",
             }
           """
    ).build();
    videoFileManagerAWSAdapter.receiveStatusUpdate(sample);
    verifyNoMoreInteractions(videoCutterJpaRepository);
  }

  @Test
  @DisplayName("Given video with finished status and with url "
      + "When update video status "
      + "Then update url and status successfully")
  void updateUrlAndStatusSuccessfully() {
    Message sample = Message.builder().body(
        """
            {
                 "fileId": "file-key",
                 "status": "FINISHED",
                 "url": "http://localhost:8080"
             }
           """
    ).build();
    videoFileManagerAWSAdapter.receiveStatusUpdate(sample);
    verify(videoCutterJpaRepository, times(1)).updateVideoCutterUrl(eq("http://localhost:8080"), eq("file-key"), any(LocalDateTime.class));
    verify(videoCutterJpaRepository, times(1)).updateVideoCutterProcessingStatus(eq("file-key"), eq(VideoProcessingStatusEnum.FINISHED), any(LocalDateTime.class));
  }

}
