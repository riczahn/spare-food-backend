package de.thb.sparefood.user.service;

import de.thb.sparefood.auth.model.BasicAuthDTO;
import de.thb.sparefood.auth.service.AuthenticationHelper;
import de.thb.sparefood.user.exception.UnknownUserException;
import de.thb.sparefood.user.model.User;
import de.thb.sparefood.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

  private UserRepository userRepository;
  private AuthenticationHelper authenticationHelper;
  private UserService userService;

  private final String anyEmail = "anyMail@test.de";

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    authenticationHelper = mock(AuthenticationHelper.class);
    userService = new UserService(userRepository, authenticationHelper);
  }

  @Test
  void givenCorrectCredentialsExpectsTrue() throws UnknownUserException {
    User anyUser = new User(anyEmail, "any", "any", "any");
    BasicAuthDTO correctCredentials = new BasicAuthDTO(anyEmail, "any");

    when(userRepository.findByEmail(anyEmail)).thenReturn(Optional.of(anyUser));
    when(authenticationHelper.isPasswordCorrect(any(), any())).thenReturn(true);

    boolean actualResult = userService.isCorrectPasswordProvided(correctCredentials);

    assertThat(actualResult).isTrue();
  }

  @Test
  void givenWrongCredentialsExpectsFalse() throws UnknownUserException {
    User anyUser = new User(anyEmail, "any", "any", "any");
    BasicAuthDTO correctCredentials = new BasicAuthDTO(anyEmail, "any");

    when(userRepository.findByEmail(anyEmail)).thenReturn(Optional.of(anyUser));
    when(authenticationHelper.isPasswordCorrect(any(), any())).thenReturn(false);

    boolean actualResult = userService.isCorrectPasswordProvided(correctCredentials);

    assertThat(actualResult).isFalse();
  }

  @Test
  void tryingToLoginAsAnUnknownUserThrowsException() {
    String unknownEmail = "unknown@Mail.de";
    BasicAuthDTO dtoWithNonExistentUser = new BasicAuthDTO(unknownEmail, "any");

    when(userRepository.findByEmail(unknownEmail)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.isCorrectPasswordProvided(dtoWithNonExistentUser))
        .isInstanceOf(UnknownUserException.class);
  }
}
