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
            System.out.println("=== BACKEND DEBUG ===");
            System.out.println("Received mealData: " + mealData);
            
            String mealName = (String) mealData.get("mealName");
            Object caloriesObj = mealData.get("calories");
            String dateStr = (String) mealData.get("date");
            String userEmail = (String) mealData.get("userEmail");
            
            System.out.println("mealName: " + mealName);
            System.out.println("caloriesObj: " + caloriesObj);
            System.out.println("dateStr: " + dateStr);
            System.out.println("userEmail: " + userEmail);
            System.out.println("userEmail type: " + (userEmail != null ? userEmail.getClass().getSimpleName() : "null"));
            
            if (mealName == null || mealName.trim().isEmpty()) { return ResponseEntity.badRequest().build(); }
            Integer calories = caloriesObj instanceof Integer ? (Integer) caloriesObj : Integer.parseInt((String) caloriesObj);
            if (calories <= 0) { return ResponseEntity.badRequest().build(); }
            LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
            
            System.out.println("Creating Meal with userEmail: " + userEmail);
            Meal meal = new Meal(mealName.trim(), calories, date, userEmail);
            System.out.println("Meal created: " + meal);
            
            Meal savedMeal = mealService.addMeal(meal);
            System.out.println("Meal saved: " + savedMeal);
            return ResponseEntity.ok(savedMeal);
        } catch (Exception e) { 
            System.out.println("Error in addMeal: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build(); 
        }
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
    
    // Get meals by user email
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<Meal>> getMealsByUserEmail(@PathVariable String userEmail) {
        try {
            List<Meal> meals = mealService.getMealsByUserEmail(userEmail);
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
