package de.thb.sparefood.meals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.thb.sparefood.user.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meal")
@Data
@NoArgsConstructor
public class Meal {
  @Id @GeneratedValue private Long id;
  private String name;
  private String description;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  private User reservingUser;

  @OneToOne
  private Location location;

  @ElementCollection(targetClass = Property.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "meal_properties", joinColumns = @JoinColumn(name = "meal_id"))
  @Enumerated(EnumType.STRING)
  private Set<Property> properties;

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
    this(name, description, new HashSet<>(), user);
  }

  public Meal(String name, String description, Set<Property> properties, User user) {
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
