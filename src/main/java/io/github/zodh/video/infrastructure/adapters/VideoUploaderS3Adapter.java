package io.github.zodh.video.infrastructure.adapters;

import io.github.zodh.video.application.gateway.VideoUploaderGateway;
import io.github.zodh.video.domain.model.video.VideoCutter;
import org.springframework.stereotype.Component;

@Component
public class VideoUploaderS3Adapter implements VideoUploaderGateway {

  @Override
  public String upload(VideoCutter videoCutter) {
    return "teste";
  }
}
