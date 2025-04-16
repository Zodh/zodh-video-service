package io.github.zodh.video.domain.model.video;

public enum VideoProcessingStatusEnum {
  RECEIVING("O sistema recebeu a intenção do usuário de criar o vídeo."),
  AWAITING_UPLOAD("O sistema está aguardando o upload do vídeo por parte do usuário."),
  AWAITING_PROCESSING("O vídeo está em fila de processamento e em breve será processado."),
  PROCESSING("O video está sendo processado!"),
  FINISHED("O processamento do vídeo foi finalizado!"),
  ERROR("Houve um erro ao processar o vídeo!"),
  VIDEO_NOT_UPLOADED_BY_USER("O usuário não realizou o upload do vídeo!");

  private final String description;

  VideoProcessingStatusEnum(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
