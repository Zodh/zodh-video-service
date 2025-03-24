package io.github.zodh.video.application.model.upload;

import java.util.UUID;

public record GatewayUploadResponse(String url, String method, String fileId, String duration) {

}
