package com.example.simple.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.math.BigDecimal;

@Entity @Table(name="health_entries")
@Data
public class HealthEntry {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @Column(name="email", nullable=false) private String email;

  @Column(name="weight_kg", nullable=false) private BigDecimal weightKg;
  @Column(name="height_cm", nullable=false) private BigDecimal heightCm;

  @Column(nullable=false) private BigDecimal bmi;
  @Column(name="bmi_category", nullable=false) private String bmiCategory;

  @Lob @Column(name="meal_plan_json") private String mealPlanJson;
  @Lob @Column(name="workout_plan_json") private String workoutPlanJson;
  @Lob @Column(name="tips_json") private String tipsJson;
  @Lob @Column(name="quotes_json") private String quotesJson;

  @Column(name="created_at", nullable=false) private Instant createdAt = Instant.now();
}
