package io.github.zodh.video.infrastructure.adapters;

import io.github.zodh.video.application.gateway.EmailSenderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSenderSpringAdapter implements EmailSenderGateway {

  @Value("${video.service.email-origin}")
  private String originEmail;

  private final JavaMailSenderImpl javaMailSender;

  @Override
  public void send(String subject, String message, String target) {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setFrom(originEmail);
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setTo(target);
    simpleMailMessage.setText(message);
    javaMailSender.send(simpleMailMessage);
  }

}
