package io.github.zodh.video.domain.model.exception;

public class VideoException extends RuntimeException {

  private final String reason;
  private final String field;

  public VideoException(String field, String reason) {
    super(String.format("Error trying to manage video data. Field: %s. Reason: %s", field, reason));
    this.field = field;
    this.reason = reason;
  }

  public String getReason() {
    return reason;
  }

  public String getField() {
    return field;
  }
}
