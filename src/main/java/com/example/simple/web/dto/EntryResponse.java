package com.example.simple.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EntryResponse(
  Long id,
  String email,
  BigDecimal weightKg,
  BigDecimal heightCm,
  BigDecimal bmi,
  String bmiCategory,
  List<String> mealPlan,
  List<String> workoutPlan,
  List<String> tips,
  List<String> quotes,
  Instant createdAt
) {}
