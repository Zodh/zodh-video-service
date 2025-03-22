package io.github.zodh.video.application.model.list;

import java.util.UUID;

public record GetUserVideoByPageRequest(int page, int size, UUID userId) {

}
