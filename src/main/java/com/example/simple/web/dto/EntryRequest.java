package com.example.simple.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record EntryRequest(
  @NotNull @jakarta.validation.constraints.Email String email,
  @NotNull @DecimalMin("1.0") BigDecimal weightKg,
  @NotNull @DecimalMin("1.0") BigDecimal heightCm
) {}
