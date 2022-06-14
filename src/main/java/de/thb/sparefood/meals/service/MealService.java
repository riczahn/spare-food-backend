package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.exception.MealNotFoundException;
import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.repository.MealRepository;
import de.thb.sparefood.user.model.User;

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
  public Meal updateMeal(long id, Meal newMeal, User accessingUser) throws MealNotFoundException {
    Optional<Meal> optionalMeal = mealRepository.findByIdOptional(id);
    Meal toBeUpdatedMeal =
        optionalMeal.orElseThrow(
            () -> new MealNotFoundException(String.format("No Meal found with id %d", id)));

    if (!toBeUpdatedMeal.getCreator().getEmail().equals(accessingUser.getEmail())) {
      throw new SecurityException(
          String.format("User %s has no access on meal %d.", accessingUser.getEmail(), id));
    }

    toBeUpdatedMeal.adoptValuesFrom(newMeal);
    mealRepository.persist(toBeUpdatedMeal);

    return toBeUpdatedMeal;
  }
}
