package io.github.zodh.video.infrastructure.adapters;

import io.github.zodh.video.application.gateway.VideoRepositoryGateway;
import io.github.zodh.video.application.model.PageInformation;
import io.github.zodh.video.application.model.list.GetUserVideoByPageResponse;
import io.github.zodh.video.application.model.list.VideoBasicInfo;
import io.github.zodh.video.domain.model.video.VideoCutter;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import io.github.zodh.video.infrastructure.database.entity.VideoCutterEntity;
import io.github.zodh.video.infrastructure.database.repository.VideoCutterJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class VideoRepositoryJpaAdapter implements VideoRepositoryGateway {

  private final VideoCutterJpaRepository videoCutterJpaRepository;

  public VideoRepositoryJpaAdapter(VideoCutterJpaRepository videoCutterJpaRepository) {
    this.videoCutterJpaRepository = videoCutterJpaRepository;
  }

  @Override
  public Long save(VideoCutter videoCutter) {
    VideoCutterEntity entity = new VideoCutterEntity(
        null,
        videoCutter.getVideo().getName(),
        videoCutter.getVideo().getFormat(),
        videoCutter.getVideo().getProcessingStatus(),
        videoCutter.getVideo().getSizeInBytes(),
        videoCutter.getVideo().getUser().externalUserIdentifier(),
        videoCutter.getVideo().getUser().email(),
        videoCutter.getVideo().getCreationDateTime(),
        videoCutter.getVideo().getLastUpdateDateTime(),
        null,
        null
    );
    VideoCutterEntity result = videoCutterJpaRepository.save(entity);
    return result.getId();
  }

  @Override
  public void saveFileId(Long id, String fileId) {
    videoCutterJpaRepository.updateVideoCutterUrl(id, fileId);
  }

  @Override
  public GetUserVideoByPageResponse getUserVideoByPage(int page, int size, UUID userId) {
    page = page - 1;
    Sort sort = Sort.by(Direction.DESC, "creationDateTime");
    Pageable pageable = PageRequest.of(page, size, sort);
    Page<VideoCutterEntity> result = videoCutterJpaRepository.fetchUserVideos(userId, pageable);
    List<VideoBasicInfo> elements = result.get().map(vce -> new VideoBasicInfo(vce.getId(), vce.getName(), vce.getProcessingStatus(), vce.getUrl(), vce.getCreationDateTime())).toList();
    PageInformation pageInformation = new PageInformation(result.getNumber() + 1, result.getSize(),
        result.getTotalPages(), result.getNumberOfElements());
    return new GetUserVideoByPageResponse(elements, pageInformation);
  }

  @Override
  public void updateVideoStatus(String fileId, VideoProcessingStatusEnum processingStatus) {
    videoCutterJpaRepository.updateVideoCutterProcessingStatus(fileId, processingStatus,
        LocalDateTime.now());
  }

}
