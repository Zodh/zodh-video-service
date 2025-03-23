package io.github.zodh.video.application.gateway;

import io.github.zodh.video.application.model.list.GetUserVideoByPageResponse;
import io.github.zodh.video.domain.model.video.VideoCutter;
import java.util.UUID;

public interface VideoRepositoryGateway {

  Long save(VideoCutter videoCutter);
  void saveUrl(Long id, String videoUrl);
  GetUserVideoByPageResponse getUserVideoByPage(int page, int size, UUID userId);

}
