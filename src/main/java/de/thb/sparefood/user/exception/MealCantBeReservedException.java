package de.thb.sparefood.user.exception;

public class MealCantBeReservedException extends Exception {
  public MealCantBeReservedException(String message) {
    super(message);
  }
}
