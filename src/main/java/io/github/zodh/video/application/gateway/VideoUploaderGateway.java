package io.github.zodh.video.application.gateway;

import io.github.zodh.video.application.model.upload.GatewayUploadResponse;
import io.github.zodh.video.domain.model.video.VideoCutter;
import org.springframework.web.multipart.MultipartFile;

public interface VideoUploaderGateway {

  GatewayUploadResponse generateUploadUrl(VideoCutter videoCutter, MultipartFile multipartFile);

}
