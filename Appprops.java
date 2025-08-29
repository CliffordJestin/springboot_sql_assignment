package com.example.hiring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProps {
  private String baseUrl;
  private String generatePath;
  private String fallbackSubmitPath;
  private String name;
  private String regNo;
  private String email;
}
