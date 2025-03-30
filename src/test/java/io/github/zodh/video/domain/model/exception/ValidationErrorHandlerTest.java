package io.github.zodh.video.domain.model.exception;

import io.github.zodh.video.domain.model.validation.Error;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class ValidationErrorHandlerTest {

    @Test
    void testThrowValidationExceptionIfHasErrorsThrowsException() {
        LinkedList<Error> errors = new LinkedList<>();
        errors.add(new Error("email", "Email is invalid"));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                ValidationErrorHandler.throwValidationExceptionIfHasErrors("User", errors)
        );

        assertTrue(exception.getMessage().contains("Error validating User!"));
        assertEquals(errors, exception.getErrors());
    }

    @Test
    void testThrowValidationExceptionIfHasErrorsDoesNotThrowWhenNoErrors() {
        LinkedList<Error> errors = new LinkedList<>();

        assertDoesNotThrow(() -> ValidationErrorHandler.throwValidationExceptionIfHasErrors("User", errors));
    }

    @Test
    void testThrowValidationExceptionIfHasErrorsDoesNotThrowWhenErrorsIsNull() {
        assertDoesNotThrow(() -> ValidationErrorHandler.throwValidationExceptionIfHasErrors("User", null));
    }
}
