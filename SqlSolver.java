package com.example.hiring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class SqlSolver {

  public String pickSqlForRegNo(String regNo) {
    int lastTwo = parseLastTwoDigits(regNo);
    boolean isOdd = (lastTwo % 2) == 1;
    String resource = isOdd ? "sql/question1.sql" : "sql/question2.sql";
    log.info("RegNo {} → lastTwo={} → {}", regNo, lastTwo, (isOdd ? "ODD (Q1)" : "EVEN (Q2)"));
    return readClasspath(resource);
  }

  private int parseLastTwoDigits(String regNo) {
    String digits = regNo.replaceAll("\\D+", "");
    if (digits.length() < 2) {
      throw new IllegalArgumentException("Invalid regNo: " + regNo);
    }
    return Integer.parseInt(digits.substring(digits.length() - 2));
  }

  private String readClasspath(String path) {
    try {
      var res = new ClassPathResource(path);
      if (!res.exists()) throw new IllegalStateException("SQL file not found: " + path);
      try (var in = res.getInputStream()) {
        return new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read " + path, e);
    }
  }
}
