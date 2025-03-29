package io.github.zodh.video.application.service;

import io.github.zodh.video.application.gateway.VideoRepositoryGateway;
import io.github.zodh.video.application.gateway.VideoFileManagerGateway;
import io.github.zodh.video.application.model.upload.GatewayUploadResponse;
import io.github.zodh.video.application.model.upload.VideoUploadRequest;
import io.github.zodh.video.application.model.upload.VideoUploadResponse;
import io.github.zodh.video.domain.model.exception.ValidationErrorHandler;
import io.github.zodh.video.domain.model.user.User;
import io.github.zodh.video.domain.model.validation.Error;
import io.github.zodh.video.domain.model.video.Video;
import io.github.zodh.video.domain.model.video.VideoCutter;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import java.time.LocalDateTime;
import java.util.LinkedList;

public class VideoUploadUseCase {

  private final VideoRepositoryGateway videoRepositoryGateway;
  private final VideoFileManagerGateway videoFileManagerGateway;

  public VideoUploadUseCase(VideoRepositoryGateway videoRepositoryGateway,
      VideoFileManagerGateway videoFileManagerGateway) {
    this.videoRepositoryGateway = videoRepositoryGateway;
    this.videoFileManagerGateway = videoFileManagerGateway;
  }

  public VideoUploadResponse execute(VideoUploadRequest request) {
    User user = new User(request.email(), request.userId());
    LinkedList<Error> userErrors = user.validate();
    ValidationErrorHandler.throwValidationExceptionIfHasErrors("user", userErrors);
    Video video = new Video( request.videoName(), request.format(), request.sizeInBytes(), user, LocalDateTime.now(), LocalDateTime.now(), VideoProcessingStatusEnum.RECEIVING);
    LinkedList<Error> videoErrors = video.validate();
    ValidationErrorHandler.throwValidationExceptionIfHasErrors("video", videoErrors);
    VideoCutter videoCutter = new VideoCutter(video, request.cutIntervalInSeconds());
    LinkedList<Error> videoCutterErrors = videoCutter.validate();
    ValidationErrorHandler.throwValidationExceptionIfHasErrors("videoCutter", videoCutterErrors);

    Long videoCutterId = videoRepositoryGateway.save(videoCutter);
    videoCutter.setIdentifier(videoCutterId);

    GatewayUploadResponse uploadResponse = videoFileManagerGateway.generateUploadUrl(videoCutter, request.multipartFile());
    videoRepositoryGateway.saveFileId(videoCutter.getIdentifier(), uploadResponse.fileId());
    video.updateStatus(VideoProcessingStatusEnum.AWAITING_UPLOAD);
    videoRepositoryGateway.updateVideoStatus(uploadResponse.fileId(), video.getProcessingStatus());

    return new VideoUploadResponse(
        String.format("Video upload request received successfully! Please, upload the video in the following URL by using the indicated http method in the next %s.", uploadResponse.duration()),
        uploadResponse.url(),
        uploadResponse.method()
    );
  }

}
