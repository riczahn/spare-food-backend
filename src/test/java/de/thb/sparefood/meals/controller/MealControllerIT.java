package de.thb.sparefood.meals.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@QuarkusTest
class MealControllerIT {

  @Test
  void givenNoMealsReturnAnEmptyList() {
    given().when().get("/meals").then().statusCode(200).body(is("[]"));
  }
}
