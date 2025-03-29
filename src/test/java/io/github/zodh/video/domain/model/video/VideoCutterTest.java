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

class VideoCutterTest {

  @Test
  @DisplayName("Given Valid Video Cutter "
      + "When Validate "
      + "Then Throws No Errors ")
  void validateVideoCutterSuccessfully() {
    LocalDateTime sampleLdt = LocalDateTime.of(2025, Month.MARCH, 29, 10, 54);
    UUID userId = UUID.randomUUID();
    User user = new User("test@test.com", userId);
    Video video = new Video( "example-video.mp4", "mp4", 5120L, user, sampleLdt, sampleLdt, VideoProcessingStatusEnum.FINISHED);
    VideoCutter videoCutter = new VideoCutter(video, 5);
    LinkedList<Error> result = videoCutter.validate();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Given Invalid Video Cutter "
      + "When Video Is Null "
      + "And Has Not Valid Cut Interval In Seconds "
      + "Then Return Errors ")
  void validateVideoCutterWithErrors() {
    VideoCutter videoCutter = new VideoCutter(null, 0);
    LinkedList<Error> result = videoCutter.validate();
    Error e1 = new Error("video", "The video cutter must have a video to cut!");
    Error e2 = new Error("cutIntervalInSeconds", "Interval in seconds must be positive!");
    assertThat(result).isNotEmpty().containsExactlyInAnyOrder(e1, e2);
  }

}
