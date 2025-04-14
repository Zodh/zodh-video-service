package io.github.zodh.video.infrastructure.security;

import io.github.zodh.video.infrastructure.configuration.AwsVideoServiceConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

  @Value("${spring.cloud.aws.region.static}")
  private String region;
  private final AwsVideoServiceConfig awsVideoServiceConfig;

  @Autowired
  public JwtFilter(AwsVideoServiceConfig awsVideoServiceConfig) {
    this.awsVideoServiceConfig = awsVideoServiceConfig;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);

      try {
        GetUserResponse userResponse = getUser(token);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userResponse.username(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        mutableRequest.putHeader("x-user-id", userResponse.username());
        mutableRequest.putHeader("x-user-email", userResponse.userAttributes().stream().filter(at -> StringUtils.isNotBlank(at.name()) && at.name().equals("email")).findFirst().orElseThrow().value());
      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    } else {
      throw new RuntimeException("Sign in to use zodh video services!");
    }
    filterChain.doFilter(mutableRequest, response);
  }

  private GetUserResponse getUser(String token) {
    StaticCredentialsProvider scp = awsVideoServiceConfig.getCredentials();
    try(CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
        .credentialsProvider(scp)
        .region(Region.of(region))
        .build()) {
      GetUserRequest request = GetUserRequest.builder().accessToken(token).build();
      GetUserResponse response = cognitoClient.getUser(request);
      if (response.userAttributes().isEmpty()) {
        throw new RuntimeException("User not found!");
      }
      return response;
    } catch (Exception e) {
      throw new RuntimeException("Error trying to fetch user data!");
    }
  }
}