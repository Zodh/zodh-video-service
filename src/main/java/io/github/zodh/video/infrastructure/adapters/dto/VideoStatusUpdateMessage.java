package io.github.zodh.video.infrastructure.adapters.dto;

import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;

public record VideoStatusUpdateMessage(String fileId, VideoProcessingStatusEnum status, String url) {

}
