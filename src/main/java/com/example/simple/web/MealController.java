package com.example.simple.web;

import com.example.simple.model.Meal;
import com.example.simple.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import java.util.Map;
@RestController
@RequestMapping("/api/meals")
@CrossOrigin(origins = "*")
public class MealController {
    
    @Autowired
    private MealService mealService;
    
    // Get all meals
    @GetMapping
    public ResponseEntity<List<Meal>> getAllMeals() {
        try {
            List<Meal> meals = mealService.getAllMeals();
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get meal by ID
    @GetMapping("/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable Long id) {
        try {
            Optional<Meal> meal = mealService.getMealById(id);
            return meal.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Add new meal
    // Add new meal
    @PostMapping
    public ResponseEntity<Meal> addMeal(@RequestBody Map<String, Object> mealData) {
        try {
            String mealName = (String) mealData.get("mealName");
            Object caloriesObj = mealData.get("calories");
            String dateStr = (String) mealData.get("date");
            if (mealName == null || mealName.trim().isEmpty()) { return ResponseEntity.badRequest().build(); }
            Integer calories = caloriesObj instanceof Integer ? (Integer) caloriesObj : Integer.parseInt((String) caloriesObj);
            if (calories <= 0) { return ResponseEntity.badRequest().build(); }
            LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
            Meal meal = new Meal(mealName.trim(), calories, date);
            Meal savedMeal = mealService.addMeal(meal);
            return ResponseEntity.ok(savedMeal);
        } catch (Exception e) { return ResponseEntity.internalServerError().build(); }
    }
    
    // Update meal
    @PutMapping("/{id}")
    public ResponseEntity<Meal> updateMeal(@PathVariable Long id, @RequestBody Meal meal) {
        try {
            Meal updatedMeal = mealService.updateMeal(id, meal);
            return ResponseEntity.ok(updatedMeal);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Delete meal
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        try {
            mealService.deleteMeal(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get meals by date
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Meal>> getMealsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Meal> meals = mealService.getMealsByDate(date);
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get meals by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<Meal>> getMealsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Meal> meals = mealService.getMealsByDateRange(startDate, endDate);
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get total calories for a specific date
    @GetMapping("/calories/date/{date}")
    public ResponseEntity<Integer> getTotalCaloriesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            Integer totalCalories = mealService.getTotalCaloriesByDate(date);
            return ResponseEntity.ok(totalCalories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Search meals by name
    @GetMapping("/search")
    public ResponseEntity<List<Meal>> searchMealsByName(@RequestParam String name) {
        try {
            List<Meal> meals = mealService.searchMealsByName(name);
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
