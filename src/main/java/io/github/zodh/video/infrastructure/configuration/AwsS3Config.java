package io.github.zodh.video.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsS3Config {

  @Value("${spring.cloud.aws.region.static}")
  private String region;
  @Value("${spring.cloud.aws.credentials.access-key}")
  private String accessKey;
  @Value("${spring.cloud.aws.credentials.secret-key}")
  private String secretKey;
  @Value("${spring.cloud.aws.credentials.session.token}")
  private String sessionToken;

  public AwsS3Config() {
  }

  @Bean
  public S3Client getClient() {
    AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKey, secretKey, sessionToken));
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(awsCredentialsProvider)
        .build();
  }

  @Bean
  public S3Presigner getPreSigner() {
    AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKey, secretKey, sessionToken));
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(awsCredentialsProvider)
        .s3Client(getClient())
        .build();
  }

}
