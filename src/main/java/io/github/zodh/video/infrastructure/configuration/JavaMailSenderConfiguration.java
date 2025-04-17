package io.github.zodh.video.infrastructure.configuration;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class JavaMailSenderConfiguration {


  @Value("${spring.mail.host}")
  private String host;
  @Value("${spring.mail.port}")
  private String port;
  @Value("${spring.mail.username}")
  private String username;
  @Value("${spring.mail.password}")
  private String password;

  @Bean
  public JavaMailSenderImpl getJavaMailSender() {
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost(host);
    javaMailSender.setPort(Integer.parseInt(port));
    javaMailSender.setUsername(username);
    javaMailSender.setPassword(password);

    Properties props = javaMailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");

    return javaMailSender;
  }

}
