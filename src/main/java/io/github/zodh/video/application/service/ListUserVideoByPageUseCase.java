package io.github.zodh.video.application.service;

import io.github.zodh.video.application.gateway.VideoRepositoryGateway;
import io.github.zodh.video.application.model.list.GetUserVideoByPageRequest;
import io.github.zodh.video.application.model.list.GetUserVideoByPageResponse;

public class ListUserVideoByPageUseCase {

  private final VideoRepositoryGateway videoRepositoryGateway;

  public ListUserVideoByPageUseCase(VideoRepositoryGateway videoRepositoryGateway) {
    this.videoRepositoryGateway = videoRepositoryGateway;
  }

  public GetUserVideoByPageResponse execute(GetUserVideoByPageRequest request) {
    return videoRepositoryGateway.getUserVideoByPage(request.page(), request.size(), request.userId());
  }

}
