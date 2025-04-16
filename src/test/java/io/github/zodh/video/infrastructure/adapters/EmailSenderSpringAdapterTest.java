package io.github.zodh.video.infrastructure.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class EmailSenderSpringAdapterTest {

  @InjectMocks
  private EmailSenderSpringAdapter emailSenderSpringAdapter;

  @Mock
  private JavaMailSender javaMailSender;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(emailSenderSpringAdapter, "originEmail", "test@test.com");
  }

  @Test
  @DisplayName("Given valid subject, message and target "
      + "When send "
      + "Then send mail successfully")
  void sendMailMessageSuccessfully() {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setFrom("test@test.com");
    simpleMailMessage.setSubject("test");
    simpleMailMessage.setTo("txt@test.com");
    simpleMailMessage.setText("only a test msg");
    doNothing().when(javaMailSender).send(simpleMailMessage);

    emailSenderSpringAdapter.send(
        "test",
        "only a test msg",
        "txt@test.com"
    );
    verify(javaMailSender, times(1)).send(simpleMailMessage);
  }

}
