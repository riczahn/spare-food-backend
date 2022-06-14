package de.thb.sparefood.meals.model;

import de.thb.sparefood.user.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MealTest {

  @Test
  void updatingFieldsOfMealWithFieldsFromAnotherMeal() {
    Meal originalMeal = new Meal("Any Name", "Any Description", new User());

    Meal updateMeal = new Meal("New Name", "New Description", new User());

    originalMeal.adoptValuesFrom(updateMeal);

    assertThat(originalMeal.getName()).isEqualTo("New Name");
    assertThat(originalMeal.getDescription()).isEqualTo("New Description");
  }
}
