package io.github.zodh.video.domain.model.exception;

import io.github.zodh.video.domain.model.validation.Error;
import java.util.LinkedList;
import java.util.Objects;

public class ValidationErrorHandler {

  public static void throwValidationExceptionIfHasErrors(String subject, LinkedList<Error> errors) {
    if (Objects.nonNull(errors) && !errors.isEmpty()) {
      throw new ValidationException(String.format("Error validating %s!", subject), errors);
    }
  }

}
