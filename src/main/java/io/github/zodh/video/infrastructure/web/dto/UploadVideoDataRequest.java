package io.github.zodh.video.infrastructure.web.dto;

public record UploadVideoDataRequest(
    int cutIntervalInSeconds,
    String fileName,
    String fileExtension,
    long fileSize,
    String contentType
) {

}
