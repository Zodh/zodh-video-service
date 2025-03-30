package io.github.zodh.video.domain.model.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnsupportedFormatExceptionTest {

    @Test
    void testUnsupportedFormatExceptionMessage() {
        String reason = "Formato não suportado";
        UnsupportedFormatException exception = new UnsupportedFormatException(reason);

        assertEquals("format", exception.getField());
        assertEquals(reason, exception.getReason());
        assertTrue(exception.getMessage().contains("Error trying to manage video data"));
        assertTrue(exception.getMessage().contains("Field: format"));
        assertTrue(exception.getMessage().contains("Reason: " + reason));
    }
}
