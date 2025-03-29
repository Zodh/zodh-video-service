package io.github.zodh.video.domain.model.video;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.zodh.video.domain.model.user.User;
import io.github.zodh.video.domain.model.validation.Error;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedList;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VideoTest {

  @Test
  @DisplayName("Given Valid Video "
      + "When Validate "
      + "Then Return No Errors")
  void validateVideoWithoutErrors() {
    LocalDateTime sampleLdt = LocalDateTime.of(2025, Month.MARCH, 29, 10, 54);
    UUID userId = UUID.randomUUID();
    User user = new User("test@test.com", userId);
    Video video = new Video( "example-video.mp4", "mp4", 5120L, user, sampleLdt, sampleLdt, VideoProcessingStatusEnum.FINISHED);
    LinkedList<Error> result = video.validate();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Given Video Without Name, Size and User "
      + "When Validate "
      + "Then Return Errors")
  void validateVideoWithErrors() {
    LocalDateTime sampleLdt = LocalDateTime.of(2025, Month.MARCH, 29, 10, 54);
    Video video = new Video( "", "mp4", 0L, null, sampleLdt, sampleLdt, VideoProcessingStatusEnum.AWAITING_UPLOAD);
    LinkedList<Error> result = video.validate();

    Error e1 = new Error("name", "Invalid video name!");
    Error e2 = new Error("sizeInBytes", "Invalid video file! Must have more than 0 bytes.");
    Error e3 = new Error("user", "A video must have a user!");
    assertThat(result)
        .isNotEmpty()
        .containsExactlyInAnyOrder(e1, e2, e3);
  }

}
