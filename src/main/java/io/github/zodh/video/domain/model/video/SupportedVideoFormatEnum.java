package io.github.zodh.video.domain.model.video;

import io.github.zodh.video.domain.model.exception.UnsupportedFormatException;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public enum SupportedVideoFormatEnum {
  MP4("mp4");

  SupportedVideoFormatEnum(String extension) {
    this.extension = extension;
  }

  private String extension;

  public String getExtension() {
    return extension;
  }

  public static SupportedVideoFormatEnum ofFormat(String format) {
    return Arrays.stream(SupportedVideoFormatEnum.values())
      .filter(videoFormat -> StringUtils.isNotBlank(format) && videoFormat.getExtension().equals(format))
      .findFirst()
      .orElseThrow(() -> new UnsupportedFormatException("Invalid video format!"));
  }

}
