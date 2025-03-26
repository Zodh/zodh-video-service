package io.github.zodh.video.infrastructure.database.repository;

import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import io.github.zodh.video.infrastructure.database.entity.VideoCutterEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoCutterJpaRepository extends JpaRepository<VideoCutterEntity, Long> {

  @Modifying
  @Query(value = "UPDATE VideoCutterEntity vce "
      + "SET vce.fileId = :fileId "
      + "WHERE vce.id = :id")
  void updateVideoCutterUrl(@Param("id") Long id, @Param("fileId") String fileId);

  @Query(value = "SELECT new io.github.zodh.video.infrastructure.database.entity.VideoCutterEntity(vce.id, vce.name, vce.processingStatus, vce.url, vce.creationDateTime) "
      + "FROM VideoCutterEntity vce "
      + "WHERE vce.userIdentifier = :userId "
      + "AND vce.processingStatus <> 'VIDEO_NOT_UPLOADED_BY_USER' ")
  Page<VideoCutterEntity> fetchUserVideos(@Param("userId") UUID userId, Pageable pageable);

  @Modifying
  @Query(value = "UPDATE VideoCutterEntity vce "
      + "SET vce.processingStatus = :status "
      + "WHERE vce.fileId = :fileId")
  void updateVideoCutterProcessingStatus(@Param("fileId") String fileId, @Param("status") VideoProcessingStatusEnum status);

  @Query(value = "SELECT vce.id "
      + "FROM VideoCutterEntity vce "
      + "WHERE vce.processingStatus = 'AWAITING_UPLOAD' "
      + "AND vce.creationDateTime <= :minutesAgo")
  List<Long> fetchVideosNotUploadedInMinutes(@Param("minutesAgo") LocalDateTime minutesAgo);

  @Modifying
  @Query(value = "UPDATE VideoCutterEntity vce "
      + "SET vce.processingStatus = 'VIDEO_NOT_UPLOADED_BY_USER' "
      + "WHERE vce.id IN (:videoIds)")
  void invalidateVideosToUpload(@Param("videoIds") List<Long> videoIds);

  @Modifying
  @Query(value = "UPDATE VideoCutterEntity vce "
      + "SET vce.url = :url "
      + "WHERE vce.fileId = :fileId")
  void updateVideoCutterUrl(@Param("url") String url, @Param("fileId") String fileId);

}
