package io.github.zodh.video.infrastructure.adapters.exception;

public class InvalidStatusUpdateMessage extends RuntimeException {

  public InvalidStatusUpdateMessage() {
    super("A problem occurred trying to update video status!");
  }
}
