package de.thb.sparefood.meals.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thb.sparefood.PostgresResource;
import de.thb.sparefood.auth.model.BasicAuthDTO;
import de.thb.sparefood.auth.token.TokenUtils;
import de.thb.sparefood.meals.model.Location;
import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.model.Property;
import de.thb.sparefood.user.exception.UnknownUserException;
import de.thb.sparefood.user.service.UserService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.with;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
class MealControllerIT {

  private static final Double ANY_SEARCH_RADIUS = 1.0;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private Meal anyMeal;
  private String tokenForTestUser;
  private String tokenForADifferentUser;
  private static final String ANY_LONGITUDE = "52";
  private static final String ANY_LATITUDE = "56";
  private static final Location ANY_LOCATION_IN_RANGE =
      new Location(Double.valueOf(ANY_LONGITUDE), Double.valueOf(ANY_LATITUDE));

  @BeforeEach
  void setUpClass() throws UnknownUserException {
    UserService userService = mock(UserService.class);
    Mockito.when(userService.isCorrectPasswordProvided(any())).thenReturn(true);

    TokenUtils tokenUtils = new TokenUtils(userService);

    this.tokenForTestUser =
        tokenUtils.generateToken(new BasicAuthDTO("testuser@test.de", "password"));
    this.tokenForADifferentUser =
        tokenUtils.generateToken(new BasicAuthDTO("anotherUser@test.de", "password"));
    this.anyMeal = new Meal("any meal", ANY_LOCATION_IN_RANGE);
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
  void queryingForAMealOfAnotherUserReturns403() throws JsonProcessingException {
    Meal createdMeal = createMealViaApi(anyMeal, tokenForTestUser);

    with()
        .header("Authorization", "Bearer " + tokenForADifferentUser)
        .when()
        .get("/meals/{id}", createdMeal.getId())
        .then()
        .statusCode(403);

    deleteMealByIdViaApi(createdMeal.getId(), tokenForTestUser);
  }

  @Test
  void aCreatedMealCanBeRetrievedByItsId() throws JsonProcessingException {
    Meal createdMeal = createMealViaApi(anyMeal, tokenForTestUser);

    String expectedJsonOfMeal = objectMapper.writeValueAsString(createdMeal);

    with()
        .header("Authorization", "Bearer " + tokenForTestUser)
        .when()
        .get("/meals/{id}", createdMeal.getId())
        .then()
        .statusCode(200)
        .and()
        .body(is(expectedJsonOfMeal));

    deleteMealByIdViaApi(createdMeal.getId(), tokenForTestUser);
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

  @Test
  void aCreatedMealCanBeUpdated() throws JsonProcessingException {
    Meal createdMeal = createMealViaApi(anyMeal, tokenForTestUser);
    createdMeal.setName("a changed name");
    createdMeal.setDescription("any description");
    createdMeal.setCreator(null);

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

    deleteMealByIdViaApi(createdMeal.getId(), tokenForTestUser);
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
  void twoAddedMealsAreBeingPersistedAndCanBeRetrievedAndDeleted() throws JsonProcessingException {
    Meal anyMeal = new Meal("Meal 1", ANY_LOCATION_IN_RANGE);
    Meal anyOtherMeal = new Meal("Meal 2", ANY_LOCATION_IN_RANGE);

    Meal createdMealOne = createMealViaApi(anyMeal, tokenForTestUser);
    Meal createdMealTwo = createMealViaApi(anyOtherMeal, tokenForTestUser);

    List<Meal> actualAvailableMeals =
        with()
            .header("Authorization", "Bearer " + tokenForTestUser)
            .queryParam("longitude", ANY_LONGITUDE)
            .queryParam("latitude", ANY_LATITUDE)
            .queryParam("filter.radius", ANY_SEARCH_RADIUS)
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
    assertThat(actualAvailableMeals).contains(createdMealOne, createdMealTwo);

    deleteMealByIdViaApi(createdMealOne.getId(), tokenForTestUser);
    deleteMealByIdViaApi(createdMealTwo.getId(), tokenForTestUser);
    ;
  }

  @Test
  void mealsCanBeFilteredByTheirProperties() throws JsonProcessingException {
    Meal vegetarianMeal = new Meal("Vegetarian Meal", ANY_LOCATION_IN_RANGE);
    vegetarianMeal.setProperties(Set.of(Property.VEGETARIAN, Property.VEGAN));

    Meal anyOtherMeal = new Meal("Any non vegetarian Meal", ANY_LOCATION_IN_RANGE);

    Meal createdVegetarianMeal = createMealViaApi(vegetarianMeal, tokenForTestUser);
    Meal createdMealTwo = createMealViaApi(anyOtherMeal, tokenForTestUser);

    List<Meal> allVegetarianMeals =
        with()
            .header("Authorization", "Bearer " + tokenForTestUser)
            .queryParam("filter.property", "vegetarian")
            .queryParam("filter.property", "vegan")
            .queryParam("longitude", ANY_LONGITUDE)
            .queryParam("latitude", ANY_LATITUDE)
            .queryParam("filter.radius", ANY_SEARCH_RADIUS)
            .when()
            .get("/meals")
            .then()
            .statusCode(200)
            .and()
            .extract()
            .body()
            .jsonPath()
            .getList("", Meal.class);

    assertThat(allVegetarianMeals).contains(createdVegetarianMeal).doesNotContain(anyOtherMeal);

    deleteMealByIdViaApi(createdVegetarianMeal.getId(), tokenForTestUser);
    deleteMealByIdViaApi(createdMealTwo.getId(), tokenForTestUser);
  }

  @Test
  void aMealCanBeReservedAndReleasedAndWillNotBeAvailableWhenReserved()
      throws JsonProcessingException {
    Meal anyMeal = new Meal("Meal 1", ANY_LOCATION_IN_RANGE);

    Meal createdMeal = createMealViaApi(anyMeal, tokenForTestUser);

    with()
        .header("Authorization", "Bearer " + tokenForTestUser)
        .when()
        .post("/meals/{id}/reserve", createdMeal.getId())
        .then()
        .statusCode(200);

    List<Meal> allAvailableMeals =
        with()
            .header("Authorization", "Bearer " + tokenForTestUser)
            .queryParam("longitude", ANY_LONGITUDE)
            .queryParam("latitude", ANY_LATITUDE)
            .queryParam("filter.radius", ANY_SEARCH_RADIUS)
            .when()
            .get("/meals")
            .then()
            .statusCode(200)
            .and()
            .extract()
            .body()
            .jsonPath()
            .getList("", Meal.class);

    assertThat(allAvailableMeals).doesNotContain(createdMeal);

    with()
        .header("Authorization", "Bearer " + tokenForTestUser)
        .when()
        .post("/meals/{id}/release", createdMeal.getId())
        .then()
        .statusCode(200);

    deleteMealByIdViaApi(createdMeal.getId(), tokenForTestUser);
  }

  @Test
  void onlyMealsInRangeAreBeingShown() throws JsonProcessingException {
    Meal mealInRange = new Meal("Vegetarian Meal", ANY_LOCATION_IN_RANGE);

    Location anyLocationOutOfRange = new Location(10.0, 10.0);
    Meal mealOutOfRange = new Meal("Any non vegetarian Meal", anyLocationOutOfRange);

    Meal createdMealInRange = createMealViaApi(mealInRange, tokenForTestUser);
    Meal createdMealOutOfRange = createMealViaApi(mealOutOfRange, tokenForTestUser);

    List<Meal> allAvailableMealsInRange =
        with()
            .header("Authorization", "Bearer " + tokenForTestUser)
            .queryParam("longitude", ANY_LONGITUDE)
            .queryParam("latitude", ANY_LATITUDE)
            .queryParam("filter.radius", ANY_SEARCH_RADIUS)
            .when()
            .get("/meals")
            .then()
            .statusCode(200)
            .and()
            .extract()
            .body()
            .jsonPath()
            .getList("", Meal.class);

    assertThat(allAvailableMealsInRange).containsExactly(createdMealInRange);

    deleteMealByIdViaApi(createdMealInRange.getId(), tokenForTestUser);
    deleteMealByIdViaApi(createdMealOutOfRange.getId(), tokenForTestUser);
  }

  @Test
  void notSendingLocationDetailsResultsInABadRequestResponse() {
    with()
        .header("Authorization", "Bearer " + tokenForTestUser)
        .when()
        .get("/meals")
        .then()
        .statusCode(400);
  }

  private Meal createMealViaApi(Meal meal, String token) throws JsonProcessingException {
    return with()
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + token)
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

  private void deleteMealByIdViaApi(Long mealId, String token) {
    with()
        .header("Authorization", "Bearer " + token)
        .when()
        .delete("/meals/{id}", mealId)
        .then()
        .statusCode(204);
  }
}
