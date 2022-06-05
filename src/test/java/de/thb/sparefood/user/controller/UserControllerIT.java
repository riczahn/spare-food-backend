package de.thb.sparefood.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thb.sparefood.PostgresResource;
import de.thb.sparefood.user.model.User;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
class UserControllerIT {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void aUserCanBeCreatedAndFoundAndDeletedByEmail() throws JsonProcessingException {
    User anyNonExistentUser = new User("aNewUser@mail.de", "any lastname", "any firstname", "password");
    String jsonOfUser = objectMapper.writeValueAsString(anyNonExistentUser);

    given()
        .header("Content-Type", "application/json")
        .body(jsonOfUser)
        .when()
        .post("/users")
        .then()
        .statusCode(200);

    when()
        .get("/users/{email}", anyNonExistentUser.getEmail())
        .then()
        .statusCode(200)
        .and()
        .body(is(jsonOfUser));

    when().delete("/users/{email}", anyNonExistentUser.getEmail()).then().statusCode(204);

    when().get("/users/{email}", anyNonExistentUser.getEmail()).then().statusCode(404);
  }
}
