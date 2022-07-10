package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.model.Location;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationDistanceService {

  private static final double EARTH_RADIUS_IN_KM = 6371;

  /**
   * Calculates the distance between two GPS coordinates
   *
   * @param locationOne First Location
   * @param locationTwo Second Location
   * @return The distance in km
   */
  public double getDistanceBetweenLocations(Location locationOne, Location locationTwo) {
    // convert to radians
    double lon1 = Math.toRadians(locationOne.getLongitude());
    double lon2 = Math.toRadians(locationTwo.getLongitude());
    double lat1 = Math.toRadians(locationOne.getLatitude());
    double lat2 = Math.toRadians(locationTwo.getLatitude());

    // Haversine formula
    double dlon = lon2 - lon1;
    double dlat = lat2 - lat1;
    double a =
        Math.pow(Math.sin(dlat / 2), 2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

    double c = 2 * Math.asin(Math.sqrt(a));

    return (c * EARTH_RADIUS_IN_KM);
  }
}
