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
import java.util.List;
import java.util.UUID;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
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
      HttpServletRequest request
  ) throws FileUploadException {
    boolean isMultipart = JakartaServletFileUpload.isMultipartContent(request);
    if (!isMultipart) {
      return ResponseEntity.badRequest().build();
    }
    DiskFileItemFactory diskFileItemFactory = DiskFileItemFactory.builder().setBufferSize(1024 * 1024).get();
    JakartaServletFileUpload upload = new JakartaServletFileUpload<>(diskFileItemFactory);
    List<FileItem> items = upload.parseRequest(request);
    Integer cutIntervalInSeconds = 0;
    String uploadedFileName = null;
    long uploadedFileSize = -1;
    String contentType = "mp4";
    for (FileItem item : items) {
      if (item.isFormField()) {
        if ("cutIntervalInSeconds".equals(item.getFieldName())) {
          cutIntervalInSeconds = Integer.parseInt(item.getString());
        }
      } else {
        if ("multipartFile".equals(item.getFieldName())) {
          uploadedFileName = item.getName();
          uploadedFileSize = item.getSize();
          contentType = item.getContentType();
        }
      }
    }

    VideoUploadRequest videoUploadRequest = new VideoUploadRequest(email, userId, cutIntervalInSeconds, uploadedFileName,
        FilenameUtils.getExtension(uploadedFileName), uploadedFileSize, contentType);
    return ResponseEntity.status(HttpStatus.CREATED).body(videoController.upload(videoUploadRequest));
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
