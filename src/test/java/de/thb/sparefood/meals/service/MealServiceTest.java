package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.repository.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MealServiceTest {

  private MealService mealService;
  private MealRepository mealRepository;

  @BeforeEach
  void setUp() {
    mealRepository = mock(MealRepository.class);
    mealService = new MealService(mealRepository);
  }

  @Test
  void whenNoMealsAvailableReturnAnEmptyList() {
    when(mealRepository.listAll()).thenReturn(new ArrayList<>());

    List<Meal> allAvailableMeals = mealService.getAllAvailableMeals();

    assertThat(allAvailableMeals).isEmpty();
  }

  @Test
  void whenOneMealIsAvailableReturnAListOfThatMeal() {
    Meal anyMeal = new Meal("any meal");
    when(mealRepository.listAll()).thenReturn(List.of(anyMeal));

    List<Meal> allAvailableMeals = mealService.getAllAvailableMeals();

    assertThat(allAvailableMeals).hasSize(1).containsExactly(anyMeal);
  }

  @Test
  void addingAMealCallsTheMealRepository() {
    Meal anyMeal = new Meal("any meal");

    mealService.addMeal(anyMeal);

    verify(mealRepository, times(1)).persist(anyMeal);
  }

  @Test
  void removingAMealCallsTheMealRepository() {
    Meal anyMeal = new Meal("any meal");

    mealService.removeMeal(anyMeal);

    verify(mealRepository, times(1)).delete(anyMeal);
  }
}
