package io.github.zodh.video.domain.model.exception;

import io.github.zodh.video.domain.model.validation.Error;
import java.util.LinkedList;

public class ValidationException extends RuntimeException {

  private LinkedList<Error> errors;

  public ValidationException(String message, LinkedList<Error> errors) {
    super(message);
    this.errors = errors;
  }

  public LinkedList<Error> getErrors() {
    return errors;
  }
}
