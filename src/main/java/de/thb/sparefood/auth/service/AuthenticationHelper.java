package de.thb.sparefood.auth.service;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthenticationHelper {
  public boolean isPasswordCorrect(String expectedPassword, String actualPassword) {
    return expectedPassword.equals(actualPassword);
  }
}
