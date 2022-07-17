package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.exception.MealNotFoundException;
import de.thb.sparefood.meals.model.Location;
import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.user.model.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class MealServiceIT {

  @Inject MealService mealService;
  private final User anyUser = new User("testuser@test.de", "Testuser", "Test", "password");
  private final Location ANY_LOCATION = new Location(10.0, 10.0);
  private final Meal anyMeal = new Meal("any meal", anyUser, ANY_LOCATION);

  @Test
  @TestTransaction
  void addingAMealPersistsIt() {
    int numberOfMeals = mealService.getAllMeals().size();
    Meal createdMeal = mealService.persist(anyMeal);

    List<Meal> availableMeals = mealService.getAllMeals();
    assertThat(availableMeals).hasSize(numberOfMeals + 1).contains(createdMeal);
  }

  @Test
  @TestTransaction
  void updatingAMealKeepsTheIdButUpdatesAllOtherProperties() throws MealNotFoundException {
    Meal createdMeal = mealService.persist(anyMeal);
    createdMeal.setName("updated name");
    createdMeal.setDescription("any description");

    Meal updatedMeal = mealService.updateMeal(createdMeal.getId(), createdMeal, anyUser);
    assertThat(createdMeal).isEqualTo(updatedMeal);
  }

  @Test
  @TestTransaction
  void tryingToUpdateAMealOfAnotherUserThrowsException() {
    Meal createdMeal = mealService.persist(anyMeal);
    createdMeal.setName("updated name");
    createdMeal.setDescription("any description");
    Long id = createdMeal.getId();

    User aDifferentUser = new User();
    assertThatThrownBy(() -> mealService.updateMeal(id, createdMeal, aDifferentUser))
        .isInstanceOf(SecurityException.class);
  }

  @Test
  @TestTransaction
  void removingAMealDeletesIt() {
    List<Meal> mealsBeforeTest = mealService.getAllMeals();
    Meal createdMeal = mealService.persist(anyMeal);

    mealService.removeMeal(createdMeal.getId());

    List<Meal> availableMeals = mealService.getAllMeals();
    assertThat(availableMeals).containsExactlyInAnyOrderElementsOf(mealsBeforeTest);
  }
}
