package de.thb.sparefood.meals.model;

import lombok.Data;

import java.util.List;

@Data
public class FilterCriteria {
  private List<Property> properties;
  private Location userLocation;
  private double searchRadius;
}
