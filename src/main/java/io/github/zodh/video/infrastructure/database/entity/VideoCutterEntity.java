package io.github.zodh.video.infrastructure.database.entity;

import io.github.zodh.video.domain.model.video.SupportedVideoFormatEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video_cutter")
public class VideoCutterEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_cutter_generator")
  @SequenceGenerator(name = "video_cutter_generator", sequenceName = "video_cutter_id_sequence", allocationSize = 1)
  private Long id;

  @Column(name = "video_cutter_name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "video_cutter_format")
  private SupportedVideoFormatEnum format;

  @Column(name = "video_cutter_size_in_bytes")
  private Long sizeInBytes;

  @Column(name = "video_cutter_user_id")
  private UUID userIdentifier;

  @Column(name = "video_cutter_user_email")
  private String userEmail;

  @Column(name = "video_cutter_creation_date_time", updatable = false)
  private LocalDateTime creationDateTime;

  @Column(name = "video_cutter_last_update_date_time")
  private LocalDateTime lastUpdateDateTime;

  @Column(name = "video_cutter_url")
  private String url;

  @PreUpdate
  protected void onUpdate() {
    lastUpdateDateTime = LocalDateTime.now();
  }

}
