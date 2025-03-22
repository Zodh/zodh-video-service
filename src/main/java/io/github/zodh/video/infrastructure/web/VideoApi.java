package io.github.zodh.video.infrastructure.web;

import io.github.zodh.video.application.model.list.GetUserVideoByPageResponse;
import io.github.zodh.video.application.model.upload.VideoUploadResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/videos")
public class VideoApi {

  @PostMapping
  public ResponseEntity<VideoUploadResponse> uploadVideoToCut(
      @RequestHeader(name = "x-user-id") UUID userId,
      @RequestHeader(name = "x-user-email") String email,
      MultipartFile multipartFile
  ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(null);
  }

  @GetMapping
  public ResponseEntity<GetUserVideoByPageResponse> getVideos(
      @RequestHeader(name = "x-user-id") UUID userId
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

}
