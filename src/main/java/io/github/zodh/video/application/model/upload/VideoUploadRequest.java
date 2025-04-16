package io.github.zodh.video.application.model.upload;

import java.util.UUID;

public record VideoUploadRequest(String email, UUID userId, int cutIntervalInSeconds,
                                 String videoName, String format, long sizeInBytes, String contentType) {

}
