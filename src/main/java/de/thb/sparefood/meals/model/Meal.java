package de.thb.sparefood.meals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.thb.sparefood.user.model.User;
import lombok.AllArgsConstructor;
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

  @JsonIgnore
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private User creator;

  public Meal(String name) {
    this.name = name;
  }

  public Meal(String name, User user) {
    this.name = name;
    this.creator = user;
  }

  public Meal(String name, String description, User user) {
    this.name = name;
    this.description = description;
    this.creator = user;
  }

  public void adoptValuesFrom(Meal other) {
    this.setName(other.getName());
    this.setDescription(other.getDescription());
  }
}
