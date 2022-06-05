package de.thb.sparefood.meals.model;

import de.thb.sparefood.auth.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "meal")
@Data
@NoArgsConstructor
public class Meal {
  @Id @GeneratedValue private Long id;
  private String name;
  private String description;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private User creator;

  public Meal(String name, User user) {
    this.name = name;
    this.creator = user;
  }
}
