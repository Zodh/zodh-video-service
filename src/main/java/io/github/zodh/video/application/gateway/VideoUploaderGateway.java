package io.github.zodh.video.application.gateway;

import io.github.zodh.video.domain.model.video.VideoCutter;

public interface VideoUploaderGateway {

  String upload(VideoCutter videoCutter);

}
