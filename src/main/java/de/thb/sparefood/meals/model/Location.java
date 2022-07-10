package de.thb.sparefood.meals.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Data
public class Location {
  @Id @GeneratedValue private Long id;

  private Double longitude;
  private Double latitude;

  /*
   I am aware that this is not smart but we do not have much time left..
   Therefore:
     Longitude: Everything above 0 means `North`, Everything below 0 `South`
     Latitude: Everything above 0 means `East`, Everything below 0 `West`

     imagine to have an actual validation here...
  */

  public Location(Double longitude, Double latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
  }
}
