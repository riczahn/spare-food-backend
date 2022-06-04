package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.repository.MealRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class MealService {

  @Inject MealRepository mealRepository;

  public MealService(MealRepository mealRepository) {
    this.mealRepository = mealRepository;
  }

  public List<Meal> getAllAvailableMeals() {
    return mealRepository.listAll();
  }

  @Transactional
  public Meal addMeal(Meal meal) {
    mealRepository.persist(meal);
    return meal;
  }

  public Meal findMealById(long id) {
    return mealRepository.findById(id);
  }

  @Transactional
  public void removeMeal(long mealId) {
    mealRepository.deleteById(mealId);
  }
}
