package io.github.zodh.video.domain.model.exception;

import io.github.zodh.video.domain.model.validation.Error;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationExceptionTest {

    @Test
    void testValidationExceptionMessage() {
        LinkedList<Error> errors = new LinkedList<>();
        errors.add(new Error("username", "Username is required"));
        errors.add(new Error("password", "Password must be at least 8 characters"));

        ValidationException exception = new ValidationException("Validation failed", errors);

        assertEquals("Validation failed", exception.getMessage());
        assertEquals(errors, exception.getErrors());
        assertEquals(2, exception.getErrors().size());
        assertEquals("username", exception.getErrors().get(0).field());
        assertEquals("Username is required", exception.getErrors().get(0).message());
    }
}
