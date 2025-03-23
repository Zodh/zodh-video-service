package io.github.zodh.video.application.service;

import io.github.zodh.video.application.gateway.VideoRepositoryGateway;
import io.github.zodh.video.application.gateway.VideoUploaderGateway;
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
  private final VideoUploaderGateway videoUploaderGateway;

  public VideoUploadUseCase(VideoRepositoryGateway videoRepositoryGateway,
      VideoUploaderGateway videoUploaderGateway) {
    this.videoRepositoryGateway = videoRepositoryGateway;
    this.videoUploaderGateway = videoUploaderGateway;
  }

  public VideoUploadResponse execute(VideoUploadRequest request) {
    User user = new User(request.email(), request.userId());
    LinkedList<Error> userErrors = user.validate();
    ValidationErrorHandler.throwValidationExceptionIfHasErrors("user", userErrors);
    Video video = new Video(null, request.videoName(), request.format(), request.sizeInBytes(), user, LocalDateTime.now(), LocalDateTime.now(), VideoProcessingStatusEnum.UPLOADING);
    LinkedList<Error> videoErrors = video.validate();
    ValidationErrorHandler.throwValidationExceptionIfHasErrors("video", videoErrors);
    VideoCutter videoCutter = new VideoCutter(video, request.cutIntervalInSeconds());
    LinkedList<Error> videoCutterErrors = videoCutter.validate();
    ValidationErrorHandler.throwValidationExceptionIfHasErrors("videoCutter", videoCutterErrors);

    Long videoIdentifier = videoRepositoryGateway.save(videoCutter);
    video.setIdentifier(videoIdentifier);
    String url = videoUploaderGateway.upload(videoCutter);
    videoRepositoryGateway.saveUrl(video.getIdentifier(), url);

    return new VideoUploadResponse("Video uploaded successfully! You'll receive emails about video processing soon!");
  }

}
