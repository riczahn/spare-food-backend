package de.thb.sparefood.auth.model;

import lombok.Data;

@Data
public class BasicAuthDTO {
  private String email;
  private String password;
}
