package com.example.simple.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

@Configuration
public class CorsConfig {
  @Value("${app.cors.allowed-origin}") String allowedOrigin;

  @Bean
  public CorsFilter corsFilter(){
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(List.of(allowedOrigin));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
    cfg.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return new CorsFilter(src);
  }
}
