package io.github.zodh.video.infrastructure.configuration;

import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class AwsVideoServiceConfig {

  @Value("${spring.cloud.aws.region.static}")
  private String region;
  @Value("${spring.cloud.aws.credentials.access-key}")
  private String accessKey;
  @Value("${spring.cloud.aws.credentials.secret-key}")
  private String secretKey;
  @Value("${spring.cloud.aws.credentials.session.token}")
  private String sessionToken;
  @Value("${video.status.uodate.queue-url}")
  private String queueUrl;

  public AwsVideoServiceConfig() {
  }

  @Bean
  public S3Client getClient() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(getCredentials())
        .build();
  }

  @Bean
  public S3Presigner getPreSigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(getCredentials())
        .s3Client(getClient())
        .build();
  }

  @Bean
  public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
    return SqsTemplate.builder()
        .sqsAsyncClient(sqsAsyncClient)
        .configure(o -> o.queueNotFoundStrategy(QueueNotFoundStrategy.FAIL))
        .build();
  }

  @Bean
  public SqsAsyncClient sqsAsyncClient() {
    return SqsAsyncClient.builder()
        .credentialsProvider(getCredentials())
        .region(Region.of(region))
        .endpointOverride(URI.create(queueUrl))
        .build();
  }

  public StaticCredentialsProvider getCredentials() {
    AwsSessionCredentials credentials = AwsSessionCredentials.create(accessKey, secretKey, sessionToken);
    return StaticCredentialsProvider.create(credentials);
  }

}
