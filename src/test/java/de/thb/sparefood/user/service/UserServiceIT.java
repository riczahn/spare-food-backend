package de.thb.sparefood.user.service;

import de.thb.sparefood.user.exception.UnknownUserException;
import de.thb.sparefood.user.model.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class UserServiceIT {

  @Inject UserService userService;

  @Test
  @TestTransaction
  void addingANewUserPersistsIt() {
    User anyUser = new User("any@email.de", "any lastname", "any firstname", "anyPassword");

    User createdUser = userService.addUser(anyUser);

    assertThat(createdUser).isEqualTo(anyUser);
  }

  @Test
  @TestTransaction
  void aUserCanBeCreatedAndDeleted() throws UnknownUserException {
    List<User> allUsersBeforeTest = userService.getAllUsers();
    User anyUser = new User("any@email.de", "any lastname", "any firstname", "anyPassword");
    User createdUser = userService.addUser(anyUser);

    userService.removeUserWithEmail(createdUser.getEmail());

    List<User> allUsersAfterRemoval = userService.getAllUsers();
    assertThat(allUsersAfterRemoval).containsExactlyInAnyOrderElementsOf(allUsersBeforeTest);
  }

  @Test
  void aUserCanBeFoundByItsEmail() {
    List<User> allUsers = userService.getAllUsers();
    User userToBeQueriedFor = allUsers.get(0);

    User actualUser = userService.getUserByEmail(userToBeQueriedFor.getEmail()).orElseThrow();

    assertThat(actualUser).isEqualTo(userToBeQueriedFor);
  }
}
