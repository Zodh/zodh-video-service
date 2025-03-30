package io.github.zodh.video.infrastructure.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.zodh.video.application.model.list.GetUserVideoByPageResponse;
import io.github.zodh.video.domain.model.user.User;
import io.github.zodh.video.domain.model.video.Video;
import io.github.zodh.video.domain.model.video.VideoCutter;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import io.github.zodh.video.infrastructure.database.entity.VideoCutterEntity;
import io.github.zodh.video.infrastructure.database.repository.VideoCutterJpaRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class VideoRepositoryJpaAdapterTest {

  @InjectMocks
  private VideoRepositoryJpaAdapter videoRepositoryJpaAdapter;
  @Mock
  private VideoCutterJpaRepository videoCutterJpaRepository;

  @Test
  @DisplayName("Given valid video cutter "
      + "When save video cutter entity "
      + "Then return persisted entity id ")
  void persistVideoCutterAndReturnId() {
    LocalDateTime sampleLdt = LocalDateTime.of(2025, Month.MARCH, 29, 10, 54);
    UUID userId = UUID.randomUUID();
    User user = new User("test@test.com", userId);
    Video video = new Video( "example-video.mp4", "mp4", 5120L, user, sampleLdt, sampleLdt, VideoProcessingStatusEnum.FINISHED);
    VideoCutter videoCutter = new VideoCutter(video, 5);
    VideoCutterEntity expectedResponse = new VideoCutterEntity();
    expectedResponse.setId(1L);
    when(videoCutterJpaRepository.save(any())).thenReturn(expectedResponse);
    Long result = videoRepositoryJpaAdapter.save(videoCutter);
    assertThat(result).isEqualTo(1L);
  }

  @Test
  @DisplayName("Given valid video cutter id "
      + "When update video cutter file id "
      + "And return nothing")
  void updateFileId() {
    Long id = 1L;
    String fileId = "abc";
    videoRepositoryJpaAdapter.saveFileId(id, fileId);
    verify(videoCutterJpaRepository, times(1)).updateVideoCutterUrl(id, fileId);
  }

  @Test
  @DisplayName("Given valid page, size and user id "
      + "When get user videos by page "
      + "Then return user video successfully")
  void listUserVideoSuccessfully() {
    int page = 1;
    int size = 10;
    UUID userId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "creationDateTime"));
    VideoCutterEntity vce = new VideoCutterEntity();
    vce.setId(1L);
    vce.setName("test");
    vce.setProcessingStatus(VideoProcessingStatusEnum.FINISHED);
    vce.setUrl("test.com.br/download");
    vce.setCreationDateTime(LocalDateTime.of(2025, Month.MARCH, 29, 22, 4));
    Page<VideoCutterEntity> expectedResult = new PageImpl<>(List.of(vce), pageable, 1L);
    when(videoCutterJpaRepository.fetchUserVideos(userId, pageable))
        .thenReturn(expectedResult);
    GetUserVideoByPageResponse result = videoRepositoryJpaAdapter.getUserVideoByPage(page, size, userId);
    assertThat(result).isNotNull();
    assertThat(result.videos()).isNotEmpty().hasSize(1);
    assertThat(result.videos().getFirst().identifier()).isEqualTo(1L);
    assertThat(result.videos().getFirst().name()).isEqualTo("test");
  }

  @Test
  @DisplayName("Given valid file id and video processing status enum "
      + "When update video status "
      + "Then update video successfully and return nothing ")
  void updateVideoStatusSuccessfully() {
    String fileId = "abc";
    VideoProcessingStatusEnum processingStatusEnum = VideoProcessingStatusEnum.AWAITING_PROCESSING;
    videoRepositoryJpaAdapter.updateVideoStatus(fileId, processingStatusEnum);
    verify(videoCutterJpaRepository, times(1)).updateVideoCutterProcessingStatus(eq(fileId), eq(processingStatusEnum), any());
  }

}
