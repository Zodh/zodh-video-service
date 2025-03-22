package io.github.zodh.video.application.model.list;

import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import java.time.LocalDateTime;

public record VideoBasicInfo(Long identifier, String name, VideoProcessingStatusEnum status,
                             String url, LocalDateTime creationDateTime) {

}
