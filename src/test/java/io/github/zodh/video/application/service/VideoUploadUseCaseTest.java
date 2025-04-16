package io.github.zodh.video.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.zodh.video.application.gateway.VideoFileManagerGateway;
import io.github.zodh.video.application.gateway.VideoRepositoryGateway;
import io.github.zodh.video.application.model.upload.GatewayUploadResponse;
import io.github.zodh.video.application.model.upload.VideoUploadRequest;
import io.github.zodh.video.application.model.upload.VideoUploadResponse;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class VideoUploadUseCaseTest {

  @InjectMocks
  private VideoUploadUseCase videoUploadUseCase;
  @Mock
  private VideoRepositoryGateway videoRepositoryGateway;
  @Mock
  private VideoFileManagerGateway videoFileManagerGateway;

  @Test
  @DisplayName("Given Valid Video Upload Request "
      + "When Execute Video Upload "
      + "Then Upload Video Successfully")
  void uploadVideoSuccessfully() {
    String email = "test@test.com";
    UUID userId = UUID.randomUUID();
    int cutIntervalInSeconds = 5;
    String videoName = "example-video.mp4";
    String format = "mp4";
    long sizeInBytes = 1024 * 5;
    byte[] fileBytes = new byte[1024*5];


    when(videoRepositoryGateway.save(any())).thenReturn(1L);
    when(videoFileManagerGateway.generateUploadUrl(any(), any()))
        .thenReturn(new GatewayUploadResponse("https://test-download.com", "PUT", "randomId", "1 minute"));
    VideoUploadResponse result = videoUploadUseCase.execute(new VideoUploadRequest(email, userId, cutIntervalInSeconds, videoName, format, sizeInBytes, "mp4"));

    assertThat(result).isNotNull();
    assertThat(result.uploadUrl()).isEqualTo("https://test-download.com");
    assertThat(result.message()).isEqualTo("Video upload request received successfully! Please, upload the video in the following URL by using the indicated http method in the next 1 minute.");
    assertThat(result.method()).isEqualTo("PUT");
    verify(videoRepositoryGateway, times(1)).saveFileId(1L, "randomId");
    verify(videoRepositoryGateway, times(1)).updateVideoStatus("randomId", VideoProcessingStatusEnum.AWAITING_UPLOAD);
  }

}
