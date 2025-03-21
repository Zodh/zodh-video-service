package io.github.zodh.video.domain.model.exception;

public class UnsupportedFormatException extends VideoException {

  public UnsupportedFormatException(String reason) {
    super("format", reason);
  }
}
