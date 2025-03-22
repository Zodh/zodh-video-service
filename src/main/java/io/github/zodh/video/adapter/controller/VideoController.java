package io.github.zodh.video.adapter.controller;

import io.github.zodh.video.application.model.list.GetUserVideoByPageRequest;
import io.github.zodh.video.application.model.list.GetUserVideoByPageResponse;
import io.github.zodh.video.application.model.upload.VideoUploadRequest;
import io.github.zodh.video.application.model.upload.VideoUploadResponse;
import io.github.zodh.video.application.service.ListUserVideoByPageUseCase;
import io.github.zodh.video.application.service.VideoUploadUseCase;

public class VideoController {

  private final VideoUploadUseCase videoUploadUseCase;
  private final ListUserVideoByPageUseCase listUserVideoByPageUseCase;

  public VideoController(VideoUploadUseCase videoUploadUseCase, ListUserVideoByPageUseCase listUserVideoByPageUseCase) {
    this.videoUploadUseCase = videoUploadUseCase;
    this.listUserVideoByPageUseCase = listUserVideoByPageUseCase;
  }

  public VideoUploadResponse upload(VideoUploadRequest request) {
    return videoUploadUseCase.execute(request);
  }

  public GetUserVideoByPageResponse getUserVideos(GetUserVideoByPageRequest request) {
    return listUserVideoByPageUseCase.execute(request);
  }

}
