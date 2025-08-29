package com.example.hiring.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateWebhookRequest {
  private String name;
  private String regNo;
  private String email;
}
