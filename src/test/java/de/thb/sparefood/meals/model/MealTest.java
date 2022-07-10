package de.thb.sparefood.meals.model;

import de.thb.sparefood.user.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MealTest {

  @Test
  void updatingFieldsOfMealWithFieldsFromAnotherMeal() {
    User user = new User();
    Meal originalMeal = new Meal("Any Name", "Any Description", user, new Location());

    Meal updateMeal = new Meal("New Name", "New Description", user, new Location(50.0, 50.0));

    originalMeal.adoptValuesFrom(updateMeal);

    assertThat(originalMeal.getName()).isEqualTo("New Name");
    assertThat(originalMeal.getDescription()).isEqualTo("New Description");
  }
}
