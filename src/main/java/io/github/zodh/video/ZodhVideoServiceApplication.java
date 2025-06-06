package io.github.zodh.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication
public class ZodhVideoServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ZodhVideoServiceApplication.class, args);
  }

}
