package de.thb.sparefood.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicAuthDTO {
  private String email;
  private String password;
}
