package io.github.zodh.video.infrastructure.database.repository;

import io.github.zodh.video.infrastructure.database.entity.VideoCutterEntity;
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
      + "SET vce.url = :url "
      + "WHERE vce.id = :id")
  void updateVideoCutterUrl(@Param("id") Long id, @Param("url") String url);

  @Query(value = "SELECT new io.github.zodh.video.infrastructure.database.entity.VideoCutterEntity(vce.id, vce.name, vce.processingStatus, vce.url, vce.creationDateTime) "
      + "FROM VideoCutterEntity vce "
      + "WHERE vce.userIdentifier = :userId")
  Page<VideoCutterEntity> fetchUserVideos(@Param("userId") UUID userId, Pageable pageable);

}
