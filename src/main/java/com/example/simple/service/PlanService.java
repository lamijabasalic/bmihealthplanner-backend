package com.example.simple.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PlanService {

  public record PlanResult(BigDecimal bmi, String category, List<String> meals, List<String> workouts, List<String> tips, List<String> quotes) {}

  public PlanResult generate(BigDecimal weightKg, BigDecimal heightCm){
    BigDecimal heightM = heightCm.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
    BigDecimal bmi = weightKg.divide(heightM.multiply(heightM), 2, RoundingMode.HALF_UP);
    String cat = bmiCategory(bmi);

    List<String> meals = mealPlanFor(cat);
    List<String> workouts = workoutPlanFor(cat);
    List<String> tips = tipsFor(cat);
    List<String> quotes = randomQuotes(3);

    return new PlanResult(bmi, cat, meals, workouts, tips, quotes);
  }

  private String bmiCategory(BigDecimal bmi){
    double v = bmi.doubleValue();
    if (v < 18.5) return "Underweight";
    if (v < 25.0) return "Normal weight";
    if (v < 30.0) return "Overweight";
    return "Obesity";
  }

  private List<String> mealPlanFor(String cat){
    List<String> m = new ArrayList<>();
    switch (cat){
      case "Underweight" -> {
        m.add("Breakfast: Oatmeal with banana, peanut butter, and milk");
        m.add("Lunch: Chicken wrap, quinoa salad, olive oil dressing");
        m.add("Snack: Greek yogurt + mixed nuts");
        m.add("Dinner: Salmon, sweet potato, steamed veggies");
      }
      case "Normal weight" -> {
        m.add("Breakfast: Scrambled eggs, whole-grain toast, fruit");
        m.add("Lunch: Grilled chicken bowl (rice, veggies)");
        m.add("Snack: Apple + almonds");
        m.add("Dinner: Lean beef stir-fry with brown rice");
      }
      case "Overweight" -> {
        m.add("Breakfast: Protein smoothie (whey, berries, spinach)");
        m.add("Lunch: Turkey salad (greens, avocado, vinaigrette)");
        m.add("Snack: Cottage cheese + cucumber");
        m.add("Dinner: Baked chicken, cauliflower mash, broccoli");
      }
      default -> {
        m.add("Breakfast: Veg omelet, side salad");
        m.add("Lunch: Tuna salad lettuce wraps");
        m.add("Snack: Protein yogurt");
        m.add("Dinner: Grilled fish, roasted vegetables");
      }
    }
    return m;
  }

  private List<String> workoutPlanFor(String cat){
    List<String> w = new ArrayList<>();
    switch (cat){
      case "Underweight" -> {
        w.add("3x/week full-body strength (squats, presses, rows)");
        w.add("2x/week light cardio 20–30 min");
        w.add("Progressive overload: +2.5% weights weekly");
      }
      case "Normal weight" -> {
        w.add("3–4x/week mix: strength + HIIT");
        w.add("Daily 7–10k steps");
        w.add("Mobility 10 min after sessions");
      }
      case "Overweight" -> {
        w.add("3x/week strength (compound lifts)");
        w.add("3x/week low-impact cardio 30–40 min");
        w.add("Aim for +NEAT: more walking, stairs");
      }
      default -> {
        w.add("4x/week low-impact cardio 20–30 min (bike/elliptical)");
        w.add("2–3x/week machine-based strength, full-body");
        w.add("Short walks after meals (5–10 min)");
      }
    }
    return w;
  }

  private List<String> tipsFor(String cat){
    List<String> t = new ArrayList<>();
    switch (cat){
      case "Underweight" -> {
        t.add("Eat every 3–4 hours; add healthy fats.");
        t.add("Track progress weekly; prioritize sleep 7–9h.");
        t.add("Protein ~1.6–2.2 g/kg/day.");
      }
      case "Normal weight" -> {
        t.add("Stay consistent; keep portions balanced.");
        t.add("Hydrate: 2–3 liters/day.");
        t.add("Prioritize protein and fiber each meal.");
      }
      case "Overweight" -> {
        t.add("Maintain a gentle calorie deficit (200–500 kcal).");
        t.add("Plan meals; avoid liquid calories.");
        t.add("Strength train to preserve muscle.");
      }
      default -> {
        t.add("Start small; build habits steadily.");
        t.add("Choose low-impact activities you enjoy.");
        t.add("Seek medical guidance for personalized targets.");
      }
    }
    return t;
  }

  private static final String[] QUOTES = new String[]{
    "Every step brings you closer to your goal.",
    "Your body can do it — your mind just needs to believe.",
    "Small progress is still progress.",
    "Discipline beats motivation.",
    "One workout at a time.",
    "Show up for yourself.",
    "Health is an investment, not an expense."
  };

  private List<String> randomQuotes(int count){
    Random r = new Random();
    List<String> list = new ArrayList<>();
    for (int i=0; i<count; i++){
      list.add(QUOTES[r.nextInt(QUOTES.length)]);
    }
    return list;
  }
}
