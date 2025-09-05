package com.example.simple.repo;

import com.example.simple.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    
    // Find meals by date
    List<Meal> findByDateOrderByCreatedAtDesc(LocalDate date);
    
    // Find meals by date range
    List<Meal> findByDateBetweenOrderByCreatedAtDesc(LocalDate startDate, LocalDate endDate);
    
    // Find all meals ordered by creation date (newest first)
    List<Meal> findAllByOrderByCreatedAtDesc();
    
    // Find meals by meal name containing (case insensitive)
    List<Meal> findByMealNameContainingIgnoreCaseOrderByCreatedAtDesc(String mealName);
    
    // Get total calories for a specific date
    @Query("SELECT COALESCE(SUM(m.calories), 0) FROM Meal m WHERE m.date = :date")
    Integer getTotalCaloriesByDate(LocalDate date);
    
    // Get total calories for a date range
    @Query("SELECT COALESCE(SUM(m.calories), 0) FROM Meal m WHERE m.date BETWEEN :startDate AND :endDate")
    Integer getTotalCaloriesByDateRange(LocalDate startDate, LocalDate endDate);
}
