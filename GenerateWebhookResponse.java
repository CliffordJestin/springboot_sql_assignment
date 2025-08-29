package com.example.hiring.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateWebhookResponse {
  private String webhook;
  private String accessToken;
  private String question;
}
