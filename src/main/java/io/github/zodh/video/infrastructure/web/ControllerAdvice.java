package io.github.zodh.video.infrastructure.web;

import io.github.zodh.video.domain.model.exception.UnsupportedFormatException;
import io.github.zodh.video.domain.model.exception.ValidationException;
import io.github.zodh.video.domain.model.validation.Error;
import io.github.zodh.video.domain.model.video.SupportedVideoFormatEnum;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<BaseValidationResponse> handleValidationException(ValidationException validationException) {
    BaseValidationResponse response = new BaseValidationResponse(validationException.getMessage(), validationException.getErrors());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(UnsupportedFormatException.class)
  public ResponseEntity<BaseValidationResponse> handleUnsupportedFormatException(UnsupportedFormatException unsupportedFormatException) {
    BaseValidationResponse response = new BaseValidationResponse(unsupportedFormatException.getMessage(), List.of(new Error("format", "Supported formats are: " + Arrays.stream(
        SupportedVideoFormatEnum.values()).map(SupportedVideoFormatEnum::getExtension).collect(
        Collectors.joining(", ")))));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  record BaseValidationResponse(String message, List<Error> errors) {}

}
