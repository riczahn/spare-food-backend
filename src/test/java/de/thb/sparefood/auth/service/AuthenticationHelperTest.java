package de.thb.sparefood.auth.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationHelperTest {

  private static final String CORRECT_PASSWORD = "correctPassword";
  private static final String WRONG_PASSWORD = "wrongPassword";

  private final AuthenticationHelper authenticationHelper = new AuthenticationHelper();

  @Test
  void givenTheSameStringsExpectsSuccess() {
    boolean result = authenticationHelper.isPasswordCorrect(CORRECT_PASSWORD, CORRECT_PASSWORD);
    assertThat(result).isTrue();
  }

  @Test
  void givenDifferentStringsExpectsFailure() {
    boolean result = authenticationHelper.isPasswordCorrect(CORRECT_PASSWORD, WRONG_PASSWORD);
    assertThat(result).isFalse();
  }

}