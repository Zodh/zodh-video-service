package io.github.zodh.video.application.model.upload;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record VideoUploadRequest(String email, UUID userId, int cutIntervalInSeconds,
                                 String videoName, String format, long sizeInBytes, MultipartFile multipartFile) {

}
