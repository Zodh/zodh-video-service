package io.github.zodh.video.application.gateway;

import io.github.zodh.video.application.model.list.GetUserVideoByPageResponse;
import io.github.zodh.video.domain.model.video.VideoCutter;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import java.util.UUID;

public interface VideoRepositoryGateway {

  Long save(VideoCutter videoCutter);
  void saveFileId(Long id, String fileId);
  GetUserVideoByPageResponse getUserVideoByPage(int page, int size, UUID userId);
  void updateVideoStatus(String fileId, VideoProcessingStatusEnum processingStatus);

}
