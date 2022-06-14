package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.repository.MealRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

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
    if (meal.getName() == null) {
      throw new InvalidParameterException("Missing value for property name");
    }

    mealRepository.persist(meal);
    return meal;
  }

  public Optional<Meal> findMealById(long id) {
    return mealRepository.findByIdOptional(id);
  }

  @Transactional
  public void removeMeal(long mealId) {
    mealRepository.deleteById(mealId);
  }

  @Transactional
  public Meal updateMeal(long id, Meal newMeal) {
    Meal toBeUpdatedMeal = mealRepository.findById(id);
    toBeUpdatedMeal.setName(newMeal.getName());
    toBeUpdatedMeal.setDescription(newMeal.getDescription());

    mealRepository.persist(toBeUpdatedMeal);
    return toBeUpdatedMeal;
  }
}
