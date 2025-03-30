package io.github.zodh.video.domain.model.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VideoExceptionTest {

    @Test
    void testVideoExceptionMessage() {
        String field = "resolution";
        String reason = "Resolution not supported";
        VideoException exception = new VideoException(field, reason);

        assertEquals(field, exception.getField());
        assertEquals(reason, exception.getReason());
        assertTrue(exception.getMessage().contains("Error trying to manage video data"));
        assertTrue(exception.getMessage().contains("Field: " + field));
        assertTrue(exception.getMessage().contains("Reason: " + reason));
    }
}
