package de.thb.sparefood.meals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.thb.sparefood.user.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal")
@Data
@NoArgsConstructor
public class Meal {
  @Id @GeneratedValue private Long id;
  private String name;
  private String description;

  @Column
  @Enumerated
  @ElementCollection(targetClass = Property.class, fetch = FetchType.EAGER)
  private List<Property> properties;

  @JsonIgnore
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private User creator;

  public Meal(String name) {
    this(name, null);
  }

  public Meal(String name, User user) {
    this(name, null, user);
  }

  public Meal(String name, String description, User user) {
    this(name, description, new ArrayList<>(), user);
  }

  public Meal(String name, String description, List<Property> properties, User user) {
    this.name = name;
    this.description = description;
    this.properties = properties;
    this.creator = user;
  }

  public void adoptValuesFrom(Meal other) {
    this.setName(other.getName());
    this.setDescription(other.getDescription());
  }
}
