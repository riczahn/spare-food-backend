package de.thb.sparefood.user.service;

import de.thb.sparefood.PostgresResource;
import de.thb.sparefood.user.model.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
class UserServiceIT {

  @Inject UserService userService;

  @Test
  @TestTransaction
  void addingANewUserPersistsIt() {
    User anyUser = new User("any@email.de", "any lastname", "any firstname");

    User createdUser = userService.addUser(anyUser);

    assertThat(createdUser).isEqualTo(anyUser);
  }

  @Test
  @TestTransaction
  void removingAUserDeletesTheCorrectUser() {
    List<User> allUsersBeforeRemoval = userService.getAllUsers();
    User userToBeRemoved = allUsersBeforeRemoval.get(0);

    userService.removeUserWithEmail(userToBeRemoved.getEmail());

    List<User> allUsersAfterRemoval = userService.getAllUsers();
    assertThat(allUsersAfterRemoval)
        .hasSize(allUsersBeforeRemoval.size() - 1)
        .doesNotContain(userToBeRemoved);
  }

  @Test
  void aUserCanBeFoundByItsEmail() {
    List<User> allUsers = userService.getAllUsers();
    User userToBeQueriedFor = allUsers.get(0);

    User actualUser = userService.getUserByEmail(userToBeQueriedFor.getEmail());

    assertThat(actualUser).isEqualTo(userToBeQueriedFor);
  }
}
