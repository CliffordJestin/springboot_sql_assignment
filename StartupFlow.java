package com.example.hiring.service;

import com.example.hiring.config.AppProps;
import com.example.hiring.dto.GenerateWebhookRequest;
import com.example.hiring.dto.GenerateWebhookResponse;
import com.example.hiring.dto.SubmitRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupFlow implements CommandLineRunner {

  private final AppProps props;
  private final SqlSolver solver;

  @Override
  public void run(String... args) {
    WebClient client = WebClient.builder()
        .baseUrl(props.getBaseUrl())
        .build();

    // 1. Generate webhook
    var genReq = new GenerateWebhookRequest(props.getName(), props.getRegNo(), props.getEmail());
    GenerateWebhookResponse res = client.post()
        .uri(props.getGeneratePath())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(genReq)
        .retrieve()
        .bodyToMono(GenerateWebhookResponse.class)
        .block(Duration.ofSeconds(20));

    if (res == null || !StringUtils.hasText(res.getAccessToken())) {
      throw new IllegalStateException("Failed to obtain webhook/accessToken");
    }
    log.info("Webhook: {}", res.getWebhook());

    // 2. Pick SQL
    String finalSql = solver.pickSqlForRegNo(props.getRegNo());
    try {
      Path out = Path.of("target", "final-sql-" + props.getRegNo() + ".sql");
      Files.createDirectories(out.getParent());
      Files.writeString(out, finalSql);
      log.info("Stored SQL at {}", out.toAbsolutePath());
    } catch (Exception e) {
      log.warn("Could not save SQL file: {}", e.toString());
    }

    // 3. Submit
    String submitUrl = StringUtils.hasText(res.getWebhook())
        ? res.getWebhook()
        : props.getBaseUrl() + props.getFallbackSubmitPath();

    var response = WebClient.create()
        .post()
        .uri(submitUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", res.getAccessToken())
        .bodyValue(new SubmitRequest(finalSql))
        .retrieve()
        .bodyToMono(String.class)
        .block(Duration.ofSeconds(20));

    log.info("Submission response: {}", response);
  }
}
