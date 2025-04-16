package io.github.zodh.video.application.gateway;

import io.github.zodh.video.application.model.upload.GatewayUploadResponse;
import io.github.zodh.video.domain.model.video.VideoCutter;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;

public interface VideoFileManagerGateway {

  GatewayUploadResponse generateUploadUrl(VideoCutter videoCutter, String contentType);
  void invalidateNotUploadedVideos();
  void updateVideoProcessingStatus(String fileId, VideoProcessingStatusEnum status, String url);

}
