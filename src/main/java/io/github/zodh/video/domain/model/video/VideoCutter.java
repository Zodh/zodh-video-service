package io.github.zodh.video.domain.model.video;

import io.github.zodh.video.domain.model.validation.Error;
import io.github.zodh.video.domain.model.validation.Validable;
import java.util.LinkedList;
import java.util.Objects;

public class VideoCutter implements Validable {

  private Long identifier;
  private final Video video;
  private final int cutIntervalInSeconds;

  public VideoCutter(Video video, int cutIntervalInSeconds) {
    this.video = video;
    this.cutIntervalInSeconds = cutIntervalInSeconds;
  }

  @Override
  public LinkedList<Error> validate() {
    LinkedList<Error> errors = new LinkedList<>();
    if (Objects.isNull(video)) {
      errors.add(new Error("video", "The video cutter must have a video to cut!"));
    } else {
      errors.addAll(video.validate());
    }
    if (cutIntervalInSeconds <= 0) {
      errors.add(new Error("cutIntervalInSeconds", "Interval in seconds must be positive!"));
    }
    return errors;
  }

  public void setIdentifier(Long identifier) {
    this.identifier = identifier;
  }

  public Long getIdentifier() {
    return identifier;
  }

  public Video getVideo() {
    return video;
  }

  public int getCutIntervalInSeconds() {
    return cutIntervalInSeconds;
  }

}
