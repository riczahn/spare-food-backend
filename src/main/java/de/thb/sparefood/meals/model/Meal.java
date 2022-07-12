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

  @OneToOne(cascade = CascadeType.ALL)
  private Location location;

  @ElementCollection(targetClass = Property.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "meal_properties", joinColumns = @JoinColumn(name = "meal_id"))
  @Enumerated(EnumType.STRING)
  private Set<Property> properties;

  @JsonIgnore
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private User creator;

  @JsonIgnore
  private String picturePath;

  public Meal(String name, Location location) {
    this(name, null, location);
  }

  public Meal(String name, User user, Location location) {
    this(name, null, user, location);
  }

  public Meal(String name, String description, User user, Location location) {
    this(name, description, new HashSet<>(), user, location);
  }

  public Meal(
      String name, String description, Set<Property> properties, User user, Location location) {
    this.name = name;
    this.description = description;
    this.properties = properties;
    this.creator = user;
    this.location = location;
  }

  public void adoptValuesFrom(Meal other) {
    this.setName(other.getName());
    this.setDescription(other.getDescription());
    this.setProperties(other.getProperties());
    this.setPicturePath(other.getPicturePath());
    this.setLocation(other.getLocation());
  }
}
