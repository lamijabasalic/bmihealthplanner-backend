package com.example.simple.service;

import com.example.simple.model.Meal;
import com.example.simple.repo.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MealService {
    
    @Autowired
    private MealRepository mealRepository;
    
    // Add a new meal
    public Meal addMeal(Meal meal) {
        if (meal.getDate() == null) {
            meal.setDate(LocalDate.now());
        }
        return mealRepository.save(meal);
    }
    
    // Get all meals
    public List<Meal> getAllMeals() {
        return mealRepository.findAllByOrderByCreatedAtDesc();
    }
    
    // Get meals by date
    public List<Meal> getMealsByDate(LocalDate date) {
        return mealRepository.findByDateOrderByCreatedAtDesc(date);
    }
    
    // Get meals by date range
    public List<Meal> getMealsByDateRange(LocalDate startDate, LocalDate endDate) {
        return mealRepository.findByDateBetweenOrderByCreatedAtDesc(startDate, endDate);
    }
    
    // Get meal by ID
    public Optional<Meal> getMealById(Long id) {
        return mealRepository.findById(id);
    }
    
    // Update meal
    public Meal updateMeal(Long id, Meal updatedMeal) {
        Optional<Meal> existingMeal = mealRepository.findById(id);
        if (existingMeal.isPresent()) {
            Meal meal = existingMeal.get();
            meal.setMealName(updatedMeal.getMealName());
            meal.setCalories(updatedMeal.getCalories());
            meal.setDate(updatedMeal.getDate());
            meal.setUserEmail(updatedMeal.getUserEmail());
            return mealRepository.save(meal);
        }
        throw new RuntimeException("Meal not found with id: " + id);
    }
    
    // Delete meal
    public void deleteMeal(Long id) {
        if (mealRepository.existsById(id)) {
            mealRepository.deleteById(id);
        } else {
            throw new RuntimeException("Meal not found with id: " + id);
        }
    }
    
    // Get total calories for a specific date
    public Integer getTotalCaloriesByDate(LocalDate date) {
        return mealRepository.getTotalCaloriesByDate(date);
    }
    
    // Get total calories for a date range
    public Integer getTotalCaloriesByDateRange(LocalDate startDate, LocalDate endDate) {
        return mealRepository.getTotalCaloriesByDateRange(startDate, endDate);
    }
    
    // Search meals by name
    public List<Meal> searchMealsByName(String mealName) {
        return mealRepository.findByMealNameContainingIgnoreCaseOrderByCreatedAtDesc(mealName);
    }
    
    // Get meals by user email
    public List<Meal> getMealsByUserEmail(String userEmail) {
        return mealRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }
}
