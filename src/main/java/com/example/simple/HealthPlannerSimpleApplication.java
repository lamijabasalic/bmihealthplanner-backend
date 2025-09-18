package com.example.simple;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HealthPlannerSimpleApplication {
  public static void main(String[] args) {
    // Load .env file
    Dotenv dotenv = Dotenv.configure()
        .directory("./")
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();
    
    // Set system properties from .env file
    dotenv.entries().forEach(entry -> {
      System.setProperty(entry.getKey(), entry.getValue());
    });
    
    SpringApplication.run(HealthPlannerSimpleApplication.class, args);
  }
}
