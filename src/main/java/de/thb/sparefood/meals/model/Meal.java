package de.thb.sparefood.meals.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Meal {
  @Id @GeneratedValue private Long id;
  private String name;
  private String description;

  public Meal(String name) {
    this.name = name;
  }
}
