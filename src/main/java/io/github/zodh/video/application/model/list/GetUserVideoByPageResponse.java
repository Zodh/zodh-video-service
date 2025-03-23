package io.github.zodh.video.application.model.list;

import io.github.zodh.video.application.model.PageInformation;
import java.util.List;

public record GetUserVideoByPageResponse(List<VideoBasicInfo> videos, PageInformation page) {

}
