package io.github.zodh.video.infrastructure.web;

import io.github.zodh.video.adapter.controller.VideoController;
import io.github.zodh.video.application.model.list.GetUserVideoByPageRequest;
import io.github.zodh.video.application.model.list.GetUserVideoByPageResponse;
import io.github.zodh.video.application.model.upload.VideoUploadRequest;
import io.github.zodh.video.application.model.upload.VideoUploadResponse;
import io.github.zodh.video.application.service.ListUserVideoByPageUseCase;
import io.github.zodh.video.application.service.VideoUploadUseCase;
import io.github.zodh.video.infrastructure.adapters.VideoRepositoryJpaAdapter;
import io.github.zodh.video.infrastructure.adapters.VideoFileManagerAWSAdapter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/videos")
public class VideoApi {

  private final VideoController videoController;

  public VideoApi(VideoRepositoryJpaAdapter videoRepositoryJpaAdapter, VideoFileManagerAWSAdapter videoFileManagerAWSAdapter) {
    VideoUploadUseCase videoUploadUseCase = new VideoUploadUseCase(videoRepositoryJpaAdapter, videoFileManagerAWSAdapter);
    ListUserVideoByPageUseCase listUserVideoByPageUseCase = new ListUserVideoByPageUseCase(videoRepositoryJpaAdapter);
    this.videoController = new VideoController(videoUploadUseCase, listUserVideoByPageUseCase);
  }

  @Transactional
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<VideoUploadResponse> uploadVideoToCut(
      @RequestHeader(name = "x-user-id") UUID userId,
      @RequestHeader(name = "x-user-email") String email,
      @RequestParam MultipartFile multipartFile,
      HttpServletRequest request
  ) {
    String filename = multipartFile.getOriginalFilename();
    int cutIntervalInSeconds = getIntFromStringParam(request.getParameter("cutIntervalInSeconds"));
    VideoUploadRequest videoUploadRequest = new VideoUploadRequest(email, userId, cutIntervalInSeconds, filename,
        FilenameUtils.getExtension(filename), multipartFile.getSize(), multipartFile);
    return ResponseEntity.status(HttpStatus.CREATED).body(videoController.upload(videoUploadRequest));
  }

  private int getIntFromStringParam(String param) {
    try {
      return Integer.parseInt(param);
    } catch (RuntimeException e) {
      return 0;
    }
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GetUserVideoByPageResponse> getVideos(
      @RequestHeader(name = "x-user-id") UUID userId,
      @RequestParam("page") int page,
      @RequestParam("size") int size
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(videoController.getUserVideos(new GetUserVideoByPageRequest(page, size, userId)));
  }

}
