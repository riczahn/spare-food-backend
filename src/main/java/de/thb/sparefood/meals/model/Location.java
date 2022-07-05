package de.thb.sparefood.meals.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class Location {
  @Id @GeneratedValue private Long id;

  private Double longitude;
  private Double latitude;

  public Location(Double longitude, Double latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
  }
}
