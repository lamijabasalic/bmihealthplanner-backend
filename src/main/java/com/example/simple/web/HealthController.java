package com.example.simple.web;

import com.example.simple.model.HealthEntry;
import com.example.simple.repo.HealthEntryRepository;
import com.example.simple.service.PlanService;
import com.example.simple.service.EmailService;
import com.example.simple.web.dto.EntryRequest;
import com.example.simple.web.dto.EntryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
@RequestMapping("/api")
@Tag(name = "Health Planner")
public class HealthController {

  private final HealthEntryRepository repo;
  private final PlanService service;
  private final EmailService emailService;
  private final ObjectMapper om = new ObjectMapper();
  
  // Simple in-memory storage for meals (for demo purposes)
  private static final List<Map<String, Object>> meals = new ArrayList<>();
  private static long mealIdCounter = 1;

  public HealthController(HealthEntryRepository repo, PlanService service, EmailService emailService){
    this.repo = repo; this.service = service; this.emailService = emailService;
  }

  @PostMapping("/entries")
  @Operation(summary="Create entry: compute BMI, generate plans/tips/quotes, store & return")
  public EntryResponse create(@Valid @RequestBody EntryRequest req) throws Exception {
    var result = service.generate(req.weightKg(), req.heightCm());

    var e = new HealthEntry();
    e.setEmail(req.email());
    e.setWeightKg(req.weightKg());
    e.setHeightCm(req.heightCm());
    e.setBmi(result.bmi());
    e.setBmiCategory(result.category());
    e.setMealPlanJson(om.writeValueAsString(result.meals()));
    e.setWorkoutPlanJson(om.writeValueAsString(result.workouts()));
    e.setTipsJson(om.writeValueAsString(result.tips()));
    e.setQuotesJson(om.writeValueAsString(result.quotes()));
    e = repo.save(e);

    var response = new EntryResponse(
      e.getId(), e.getEmail(), e.getWeightKg(), e.getHeightCm(), e.getBmi(), e.getBmiCategory(),
      result.meals(), result.workouts(), result.tips(), result.quotes(), e.getCreatedAt()
    );
    
    // Send email synchronously
    try {
      emailService.sendHealthPlanEmail(response);
    } catch (Exception ex) {
      System.err.println("Failed to send email: " + ex.getMessage());
      throw new RuntimeException("Failed to send email: " + ex.getMessage(), ex);
    }
    
    return response;
  }

  @GetMapping("/entries/latest")
  @Operation(summary="Get latest generated entry")
  public ResponseEntity<EntryResponse> latest() throws Exception {
    return repo.findTopByOrderByCreatedAtDesc()
      .map(this::toDto)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.noContent().build());
  }

  @GetMapping("/entries")
  @Operation(summary="List entries (newest first)")
  public List<EntryResponse> list(@RequestParam(required=false) Integer limit) throws Exception {
    var all = repo.findAll()
      .stream().sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
      .collect(Collectors.toList());
    if (limit != null && limit > 0 && limit < all.size()) {
      all = all.subList(0, limit);
    }
    return all.stream().map(this::toDto).collect(Collectors.toList());
  }

  @GetMapping("/entries/{id}")
  @Operation(summary="Get entry by ID")
  public ResponseEntity<EntryResponse> getById(@PathVariable Long id) throws Exception {
    return repo.findById(id)
      .map(this::toDto)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/entries/{id}")
  @Operation(summary="Update entry: recompute BMI, regenerate plans/tips/quotes")
  public ResponseEntity<EntryResponse> update(@PathVariable Long id, @Valid @RequestBody EntryRequest req) throws Exception {
    return repo.findById(id)
      .map(existing -> {
        try {
          var result = service.generate(req.weightKg(), req.heightCm());
          
          existing.setEmail(req.email());
          existing.setWeightKg(req.weightKg());
          existing.setHeightCm(req.heightCm());
          existing.setBmi(result.bmi());
          existing.setBmiCategory(result.category());
          existing.setMealPlanJson(om.writeValueAsString(result.meals()));
          existing.setWorkoutPlanJson(om.writeValueAsString(result.workouts()));
          existing.setTipsJson(om.writeValueAsString(result.tips()));
          existing.setQuotesJson(om.writeValueAsString(result.quotes()));
          
          var updated = repo.save(existing);
          return ResponseEntity.ok(new EntryResponse(
            updated.getId(), updated.getEmail(), updated.getWeightKg(), updated.getHeightCm(), updated.getBmi(), updated.getBmiCategory(),
            result.meals(), result.workouts(), result.tips(), result.quotes(), updated.getCreatedAt()
          ));
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      })
      .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/entries/{id}")
  @Operation(summary="Delete entry by ID")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (repo.existsById(id)) {
      repo.deleteById(id);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/test-email")
  @Operation(summary="Test email functionality")
  public String testEmail() {
    try {
      // Create a test health plan
      var testPlan = new EntryResponse(
        1L, "test@example.com", 
        new java.math.BigDecimal("70"), 
        new java.math.BigDecimal("170"), 
        new java.math.BigDecimal("24.2"), 
        "Normal weight",
        List.of("Test meal 1", "Test meal 2"),
        List.of("Test workout 1", "Test workout 2"),
        List.of("Test tip 1", "Test tip 2"),
        List.of("Test quote 1", "Test quote 2"),
        java.time.Instant.now()
      );
      
      emailService.sendHealthPlanEmail(testPlan);
      return "Test email sent successfully! Check your SendGrid dashboard.";
    } catch (Exception e) {
      return "Test email failed: " + e.getMessage();
    }
  }

  private EntryResponse toDto(HealthEntry e){
    try {
      var meals = om.readValue(e.getMealPlanJson(), new TypeReference<List<String>>(){});
      var workouts = om.readValue(e.getWorkoutPlanJson(), new TypeReference<List<String>>(){});
      var tips = om.readValue(e.getTipsJson(), new TypeReference<List<String>>(){});
      var quotes = om.readValue(e.getQuotesJson(), new TypeReference<List<String>>(){});
      return new EntryResponse(e.getId(), e.getEmail(), e.getWeightKg(), e.getHeightCm(), e.getBmi(), e.getBmiCategory(),
        meals, workouts, tips, quotes, e.getCreatedAt());
    } catch (Exception ex){
      throw new RuntimeException(ex);
    }
  }

  // ========== MEAL API ENDPOINTS ==========
  
  @GetMapping("/meals")
  @Operation(summary="Get all meals")
  public List<Map<String, Object>> getAllMeals() {
    return new ArrayList<>(meals);
  }
  
  @PostMapping("/meals")
  @Operation(summary="Add a new meal")
  public ResponseEntity<Map<String, Object>> addMeal(@RequestBody Map<String, Object> mealData) {
    try {
      // Validate required fields
      if (!mealData.containsKey("mealName") || !mealData.containsKey("calories")) {
        return ResponseEntity.badRequest().build();
      }
      
      String mealName = (String) mealData.get("mealName");
      Object caloriesObj = mealData.get("calories");
      Integer calories;
      
      if (caloriesObj instanceof Integer) {
        calories = (Integer) caloriesObj;
      } else if (caloriesObj instanceof String) {
        calories = Integer.parseInt((String) caloriesObj);
      } else {
        return ResponseEntity.badRequest().build();
      }
      
      if (mealName == null || mealName.trim().isEmpty() || calories <= 0) {
        return ResponseEntity.badRequest().build();
      }
      
      // Create meal object
      Map<String, Object> meal = new HashMap<>();
      meal.put("id", mealIdCounter++);
      meal.put("mealName", mealName.trim());
      meal.put("calories", calories);
      meal.put("date", mealData.getOrDefault("date", LocalDate.now().toString()));
      meal.put("createdAt", java.time.LocalDateTime.now().toString());
      
      // Add to list
      meals.add(0, meal); // Add to beginning for newest first
      
      return ResponseEntity.ok(meal);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
  
  @GetMapping("/meals/{id}")
  @Operation(summary="Get meal by ID")
  public ResponseEntity<Map<String, Object>> getMealById(@PathVariable Long id) {
    return meals.stream()
      .filter(meal -> meal.get("id").equals(id))
      .findFirst()
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }
  
  @PutMapping("/meals/{id}")
  @Operation(summary="Update meal by ID")
  public ResponseEntity<Map<String, Object>> updateMeal(@PathVariable Long id, @RequestBody Map<String, Object> mealData) {
    for (int i = 0; i < meals.size(); i++) {
      Map<String, Object> meal = meals.get(i);
      if (meal.get("id").equals(id)) {
        // Update fields
        if (mealData.containsKey("mealName")) {
          meal.put("mealName", mealData.get("mealName"));
        }
        if (mealData.containsKey("calories")) {
          meal.put("calories", mealData.get("calories"));
        }
        if (mealData.containsKey("date")) {
          meal.put("date", mealData.get("date"));
        }
        return ResponseEntity.ok(meal);
      }
    }
    return ResponseEntity.notFound().build();
  }
  
  @DeleteMapping("/meals/{id}")
  @Operation(summary="Delete meal by ID")
  public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
    boolean removed = meals.removeIf(meal -> meal.get("id").equals(id));
    return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }
}
