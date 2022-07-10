package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.exception.MealNotFoundException;
import de.thb.sparefood.meals.model.Location;
import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.repository.MealRepository;
import de.thb.sparefood.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MealServiceTest {

  private MealService mealService;
  private MealRepository mealRepository;
  private final User anyUser = new User();
  private final Meal anyMeal = new Meal("any meal", anyUser, new Location(10.0, 10.0));

  @BeforeEach
  void setUp() {
    mealRepository = mock(MealRepository.class);
    mealService = new MealService(mealRepository);
  }

  @Test
  void whenNoMealsAvailableReturnAnEmptyList() {
    when(mealRepository.listAll()).thenReturn(new ArrayList<>());

    List<Meal> allAvailableMeals = mealService.getAllMeals();

    assertThat(allAvailableMeals).isEmpty();
  }

  @Test
  void whenOneMealIsAvailableReturnAListOfThatMeal() {
    when(mealRepository.listAll()).thenReturn(List.of(anyMeal));

    List<Meal> allAvailableMeals = mealService.getAllMeals();

    assertThat(allAvailableMeals).hasSize(1).containsExactly(anyMeal);
  }

  @Test
  void findByIdReturnsTheMealWithThatId() {
    Meal expectedMeal = new Meal("meal with id 1", new Location(50.0, 50.0));
    when(mealRepository.findByIdOptional(1L)).thenReturn(Optional.of(expectedMeal));

    Optional<Meal> actualMeal = mealService.findMealById(1L);

    assertThat(actualMeal).contains(expectedMeal);
  }

  @Test
  void findByIdReturnsEmptyOptionalIfMealWithIdDoesNotExist() {
    when(mealRepository.findByIdOptional(1L)).thenReturn(Optional.empty());

    Optional<Meal> actualMeal = mealService.findMealById(1L);

    assertThat(actualMeal).isEmpty();
  }

  @Test
  void addingAMealCallsTheMealRepository() {
    mealService.addMeal(anyMeal);

    verify(mealRepository, times(1)).persist(anyMeal);
  }

  @Test
  void addingAMealWithoutANameThrowsAnException() {
    Meal mealWithoutName = new Meal();

    assertThatThrownBy(() -> mealService.addMeal(mealWithoutName))
        .isInstanceOf(InvalidParameterException.class);
  }

  @Test
  void updatingANonExistentMealThrowsException() {
    long anyNonExistentId = 9999L;
    when(mealRepository.findByIdOptional(anyNonExistentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> mealService.updateMeal(anyNonExistentId, anyMeal, anyUser))
        .isInstanceOf(MealNotFoundException.class);
  }

  @Test
  void removingAMealCallsTheMealRepository() {
    long anyMealId = 1L;
    mealService.removeMeal(anyMealId);

    verify(mealRepository, times(1)).deleteById(anyMealId);
  }

  @Test
  void whenMealRepositoryThrowsAnExceptionItWillBePropagated() {
    doThrow(PersistenceException.class).when(mealRepository).persist((Meal) any());

    assertThatThrownBy(() -> mealService.addMeal(anyMeal)).isInstanceOf(PersistenceException.class);
  }
}
