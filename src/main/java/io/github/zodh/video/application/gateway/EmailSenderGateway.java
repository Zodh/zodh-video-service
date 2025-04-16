package io.github.zodh.video.application.gateway;

public interface EmailSenderGateway {

  void send(String subject, String message, String target);

}
