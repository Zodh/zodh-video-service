package io.github.zodh.video.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.zodh.video.application.gateway.VideoRepositoryGateway;
import io.github.zodh.video.application.model.PageInformation;
import io.github.zodh.video.application.model.list.GetUserVideoByPageRequest;
import io.github.zodh.video.application.model.list.GetUserVideoByPageResponse;
import io.github.zodh.video.application.model.list.VideoBasicInfo;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListUserVideoByPageUseCaseTest {

  @InjectMocks
  private ListUserVideoByPageUseCase listUserVideoByPageUseCase;

  @Mock
  private VideoRepositoryGateway videoRepositoryGateway;

  @Test
  @DisplayName("Given Valid Get User Video Request "
      + "When Execute Get With Video Repository "
      + "Then Return User Videos ")
  void listUserVideosSuccessfully() {
    UUID userId = UUID.randomUUID();
    GetUserVideoByPageRequest request = new GetUserVideoByPageRequest(1, 10, userId);
    LocalDateTime creationDateTime = LocalDateTime.of(2025, Month.MARCH, 29, 10, 38);
    List<VideoBasicInfo> videoBasicInfos = List.of(
        new VideoBasicInfo(1L, "basic-video-1.mp4", VideoProcessingStatusEnum.AWAITING_PROCESSING, "https://localhost:8080/videos/1", creationDateTime),
        new VideoBasicInfo(2L, "basic-video-2.mp4", VideoProcessingStatusEnum.AWAITING_PROCESSING, "https://localhost:8080/videos/2", creationDateTime),
        new VideoBasicInfo(3L, "basic-video-3.mp4", VideoProcessingStatusEnum.AWAITING_PROCESSING, "https://localhost:8080/videos/3", creationDateTime)
    );
    PageInformation pgInfo = new PageInformation(1, 10, 1, 3);
    GetUserVideoByPageResponse expectedResponse = new GetUserVideoByPageResponse(videoBasicInfos, pgInfo);
    when(videoRepositoryGateway.getUserVideoByPage(1, 10, userId))
        .thenReturn(expectedResponse);

    GetUserVideoByPageResponse response = listUserVideoByPageUseCase.execute(request);

    assertThat(response).isNotNull();
    assertThat(response.videos()).isNotNull().containsExactlyElementsOf(videoBasicInfos);
    assertThat(response.page()).isNotNull().isEqualTo(pgInfo);
  }

}
