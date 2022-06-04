package de.thb.sparefood.meals.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thb.sparefood.meals.model.Meal;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

@QuarkusTest
class MealControllerIT {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void givenNoMealsReturnAnEmptyList() {
    when().get("/meals").then().statusCode(200).body(is("[]"));
  }

  @Test
  void twoAddedMealsAreBeingPersistedAndCanBeRetrievedAndDeleted() throws JsonProcessingException {
    Meal anyMeal = new Meal("Meal 1");
    Meal anyOtherMeal = new Meal("Meal 2");

    Meal createdMealOne = createMealViaApi(anyMeal);
    Meal createdMealTwo = createMealViaApi(anyOtherMeal);

    List<Meal> actualAvailableMeals =
        when()
            .get("/meals")
            .then()
            .statusCode(200)
            .and()
            .extract()
            .body()
            .jsonPath()
            .getList("", Meal.class);

    assertThat(createdMealOne.getName()).isEqualTo(anyMeal.getName());
    assertThat(createdMealTwo.getName()).isEqualTo(anyOtherMeal.getName());
    assertThat(actualAvailableMeals).containsExactlyInAnyOrder(createdMealOne, createdMealTwo);

    deleteMealByIdViaApi(createdMealOne.getId());
    deleteMealByIdViaApi(createdMealTwo.getId());

    // assert all meals have been deleted
    when().get("/meals").then().statusCode(200).body(is("[]"));
  }

  @Test
  void aCreatedMealCanBeRetrievedByItsId() throws JsonProcessingException {
    Meal createdMeal = createMealViaApi(new Meal("any meal"));

    String expectedJsonOfMeal = objectMapper.writeValueAsString(createdMeal);

    when()
        .get("/meals/{id}", createdMeal.getId())
        .then()
        .statusCode(200)
        .and()
        .body(is(expectedJsonOfMeal));

    deleteMealByIdViaApi(createdMeal.getId());
  }

  private Meal createMealViaApi(Meal meal) throws JsonProcessingException {
    return with()
        .header("Content-Type", "application/json")
        .body(objectMapper.writeValueAsString(meal))
        .when()
        .post("/meals")
        .then()
        .statusCode(200)
        .and()
        .extract()
        .body()
        .as(Meal.class);
  }

  private void deleteMealByIdViaApi(Long mealId) {
    when().delete("/meals/{id}", mealId).then().statusCode(204);
  }
}
