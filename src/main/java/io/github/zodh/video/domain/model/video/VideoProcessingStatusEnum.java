package io.github.zodh.video.domain.model.video;

public enum VideoProcessingStatusEnum {
  RECEIVING,
  AWAITING_UPLOAD,
  AWAITING_PROCESSING,
  PROCESSING,
  FINISHED,
  ERROR,
  VIDEO_NOT_UPLOADED_BY_USER;
}
