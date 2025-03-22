package io.github.zodh.video.infrastructure.database.repository;

import io.github.zodh.video.infrastructure.database.entity.VideoCutterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoCutterRepository extends JpaRepository<VideoCutterEntity, Long> {

}
