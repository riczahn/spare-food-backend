package de.thb.sparefood.meals.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thb.sparefood.PostgresResource;
import de.thb.sparefood.auth.model.BasicAuthDTO;
import de.thb.sparefood.auth.token.TokenUtils;
import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.user.exception.UnknownUserException;
import de.thb.sparefood.user.service.UserService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
class MealControllerIT {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private Meal anyMeal;
  private String tokenForTestUser;

  @BeforeEach
  void setUpClass() throws UnknownUserException {
    UserService userService = mock(UserService.class);
    Mockito.when(userService.isCorrectPasswordProvided(any())).thenReturn(true);

    TokenUtils tokenUtils = new TokenUtils(userService);

    this.tokenForTestUser = tokenUtils.generateToken(new BasicAuthDTO("testuser@test.de", "password"));
    this.anyMeal = new Meal("any meal");
  }

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
        with()
            .header("Authorization", "Bearer " + tokenForTestUser)
            .when()
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
    Meal createdMeal = createMealViaApi(anyMeal);

    String expectedJsonOfMeal = objectMapper.writeValueAsString(createdMeal);

    with()
        .header("Authorization", "Bearer " + tokenForTestUser)
        .when()
        .get("/meals/{id}", createdMeal.getId())
        .then()
        .statusCode(200)
        .and()
        .body(is(expectedJsonOfMeal));

    deleteMealByIdViaApi(createdMeal.getId());
  }

  @Test
  void queryingForANonExistentMealReturns404() {
    int anyNotUsedId = 99999;

    with()
        .header("Authorization", "Bearer " + tokenForTestUser)
        .when()
        .get("/meals/{id}", anyNotUsedId)
        .then()
        .statusCode(404);
  }

  @Test
  void aCreatedMealCanBeUpdated() throws JsonProcessingException {
    Meal createdMeal = createMealViaApi(anyMeal);
    createdMeal.setName("a changed name");
    createdMeal.setDescription("any description");

    String jsonOfMeal = objectMapper.writeValueAsString(createdMeal);

    with()
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + tokenForTestUser)
        .body(jsonOfMeal)
        .put("/meals/{id}", createdMeal.getId())
        .then()
        .statusCode(200)
        .and()
        .body(is(jsonOfMeal));

    deleteMealByIdViaApi(createdMeal.getId());
  }

  @Test
  void updatingANonExistentMealReturns404() throws JsonProcessingException {
    int anyNotUsedId = 99999;

    String jsonOfMeal = objectMapper.writeValueAsString(anyMeal);

    with()
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + tokenForTestUser)
        .body(jsonOfMeal)
        .put("/meals/{id}", anyNotUsedId)
        .then()
        .statusCode(404);
  }

  @Test
  void addingAMealWithoutANameReturns400() throws JsonProcessingException {
    Meal mealWithoutName = new Meal();
    String jsonOfMeal = objectMapper.writeValueAsString(mealWithoutName);

    with()
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + tokenForTestUser)
        .body(jsonOfMeal)
        .when()
        .post("/meals")
        .then()
        .statusCode(400);
  }

  private Meal createMealViaApi(Meal meal) throws JsonProcessingException {
    return with()
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + tokenForTestUser)
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
