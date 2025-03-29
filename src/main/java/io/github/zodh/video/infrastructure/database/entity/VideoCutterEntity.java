package io.github.zodh.video.infrastructure.database.entity;

import io.github.zodh.video.domain.model.video.SupportedVideoFormatEnum;
import io.github.zodh.video.domain.model.video.VideoProcessingStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "video_cutter",
    indexes = {
        @Index(name = "idx_creation_status", columnList = "video_cutter_creation_date_time, video_cutter_processing_status"),
        @Index(name = "idx_video_user", columnList = "video_cutter_user_id")
    }
)
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

  @Enumerated(EnumType.STRING)
  @Column(name = "video_cutter_processing_status")
  private VideoProcessingStatusEnum processingStatus;

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

  @Column(name = "video_cutter_file_id", unique = true)
  private String fileId;

  public VideoCutterEntity(Long id, String name, VideoProcessingStatusEnum processingStatus,
      String url, LocalDateTime creationDateTime) {
    this.id = id;
    this.name = name;
    this.processingStatus = processingStatus;
    this.url = url;
    this.creationDateTime = creationDateTime;
  }

  public VideoCutterEntity(Long id, String name, SupportedVideoFormatEnum format,
      VideoProcessingStatusEnum processingStatus, Long sizeInBytes, UUID userIdentifier,
      String userEmail, LocalDateTime creationDateTime, LocalDateTime lastUpdateDateTime,
      String url,
      String fileId) {
    this.id = id;
    this.name = name;
    this.format = format;
    this.processingStatus = processingStatus;
    this.sizeInBytes = sizeInBytes;
    this.userIdentifier = userIdentifier;
    this.userEmail = userEmail;
    this.creationDateTime = creationDateTime;
    this.lastUpdateDateTime = lastUpdateDateTime;
    this.url = url;
    this.fileId = fileId;
  }

  public VideoCutterEntity() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SupportedVideoFormatEnum getFormat() {
    return format;
  }

  public void setFormat(SupportedVideoFormatEnum format) {
    this.format = format;
  }

  public VideoProcessingStatusEnum getProcessingStatus() {
    return processingStatus;
  }

  public void setProcessingStatus(
      VideoProcessingStatusEnum processingStatus) {
    this.processingStatus = processingStatus;
  }

  public Long getSizeInBytes() {
    return sizeInBytes;
  }

  public void setSizeInBytes(Long sizeInBytes) {
    this.sizeInBytes = sizeInBytes;
  }

  public UUID getUserIdentifier() {
    return userIdentifier;
  }

  public void setUserIdentifier(UUID userIdentifier) {
    this.userIdentifier = userIdentifier;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public LocalDateTime getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(LocalDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public LocalDateTime getLastUpdateDateTime() {
    return lastUpdateDateTime;
  }

  public void setLastUpdateDateTime(LocalDateTime lastUpdateDateTime) {
    this.lastUpdateDateTime = lastUpdateDateTime;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }
}
