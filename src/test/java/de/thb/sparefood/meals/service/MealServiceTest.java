package de.thb.sparefood.meals.service;

import de.thb.sparefood.user.model.User;
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
  private final User anyUser = new User();
  private final Meal anyMeal = new Meal("any meal", anyUser);

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
    when(mealRepository.listAll()).thenReturn(List.of(anyMeal));

    List<Meal> allAvailableMeals = mealService.getAllAvailableMeals();

    assertThat(allAvailableMeals).hasSize(1).containsExactly(anyMeal);
  }

  @Test
  void addingAMealCallsTheMealRepository() {
    mealService.addMeal(anyMeal);

    verify(mealRepository, times(1)).persist(anyMeal);
  }

  @Test
  void removingAMealCallsTheMealRepository() {
    long anyMealId = 1L;
    mealService.removeMeal(anyMealId);

    verify(mealRepository, times(1)).deleteById(anyMealId);
  }

  @Test
  void findByIdReturnsTheMealWithThatId() {
    Meal expectedMeal = new Meal("meal with id 1", anyUser);
    when(mealRepository.findById(1L)).thenReturn(expectedMeal);

    Meal actualMeal = mealService.findMealById(1L);

    assertThat(actualMeal).isEqualTo(expectedMeal);
  }
}
