package de.thb.sparefood.meals.service;

import de.thb.sparefood.PostgresResource;
import de.thb.sparefood.user.model.User;
import de.thb.sparefood.meals.model.Meal;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
class MealServiceIT {

  @Inject MealService mealService;
  private final User anyUser = new User("testuser@test.de", "Testuser", "Test", "password");
  private final Meal anyMeal = new Meal("any meal", anyUser);

  @Test
  @TestTransaction
  void addingAMealPersistsIt() {
    Meal createdMeal = mealService.addMeal(anyMeal);

    List<Meal> availableMeals = mealService.getAllAvailableMeals();
    assertThat(availableMeals).hasSize(1).containsExactly(createdMeal);
  }

  @Test
  @TestTransaction
  void removingAMealDeletesIt() {
    Meal createdMeal = mealService.addMeal(anyMeal);

    mealService.removeMeal(createdMeal.getId());

    List<Meal> availableMeals = mealService.getAllAvailableMeals();
    assertThat(availableMeals).isEmpty();
  }

  @Test
  @TestTransaction
  void updatingAMealKeepsTheIdButUpdatesAllOtherProperties() {
    Meal createdMeal = mealService.addMeal(anyMeal);
    createdMeal.setName("updated name");
    createdMeal.setDescription("any description");

    Meal updatedMeal = mealService.updateMeal(createdMeal.getId(), createdMeal);
    assertThat(createdMeal).isEqualTo(updatedMeal);
  }
}
