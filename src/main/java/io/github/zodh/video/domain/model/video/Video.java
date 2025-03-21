package io.github.zodh.video.domain.model.video;

import io.github.zodh.video.domain.model.validation.Validable;
import io.github.zodh.video.domain.model.user.User;
import io.github.zodh.video.domain.model.validation.Error;
import java.util.LinkedList;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class Video implements Validable {

  private Long identifier;
  private final String name;
  private final SupportedVideoFormatEnum format;
  private final Long sizeInBytes;
  private final User user;

  public Video(Long identifier, String name, String format, Long sizeInBytes, User user) {
    this.identifier = identifier;
    this.name = name;
    this.format = SupportedVideoFormatEnum.valueOf(format);
    this.sizeInBytes = sizeInBytes;
    this.user = user;
  }

  @Override
  public LinkedList<Error> validate() {
    LinkedList<Error> errors = new LinkedList<>();
    if (StringUtils.isBlank(this.name)) {
      errors.add(new Error("name", "Invalid video name!"));
    }
    if (sizeInBytes <= 0) {
      errors.add(new Error("sizeInBytes", "Invalid video file! Must have more than 0 bytes."));
    }
    if (Objects.isNull(user)) {
      errors.add(new Error("user", "A video must have a user!"));
    } else {
      errors.addAll(user.validate());
    }
    return errors;
  }

  public void setIdentifier(Long identifier) {
    this.identifier = identifier;
  }

  public Long getIdentifier() {
    return identifier;
  }

  public String getName() {
    return name;
  }

  public SupportedVideoFormatEnum getFormat() {
    return format;
  }

  public Long getSizeInBytes() {
    return sizeInBytes;
  }

  public User getUser() {
    return user;
  }
}
