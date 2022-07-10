package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationDistanceServiceTest {

  private LocationDistanceService locationDistanceService;

  @BeforeEach
  void setUp() {
    locationDistanceService = new LocationDistanceService();
  }

  @Test
  void theDistanceBetweenTheSameLocationIs0() {
    Location location = new Location(50.0, 30.0);

    double distanceBetweenLocations =
        locationDistanceService.getDistanceBetweenLocations(location, location);

    assertThat(distanceBetweenLocations).isEqualTo(0.0);
  }

  @Test
  void canCalculateTheDistanceBetweenTwoPointsFarAwayFromOneAnother() {
    Location locationOne = new Location(50.0, 30.0);
    Location locationTwo = new Location(10.0, 10.0);

    double distanceBetweenLocations =
        locationDistanceService.getDistanceBetweenLocations(locationOne, locationTwo);

    assertThat(distanceBetweenLocations).isGreaterThan(4000);
  }

  @Test
  void canCalculateTheDistanceBetweenTheBrandenburgGateAndTheTvTowerInBerlin() {
    Location locationOfBrandenburgGate = new Location(13.380520820617676, 52.51676559448242);
    Location locationOfBerlinTvTower = new Location(13.409351348876953, 52.52082824707031);

    double distanceBetweenLocations =
        locationDistanceService.getDistanceBetweenLocations(
            locationOfBrandenburgGate, locationOfBerlinTvTower);

    // should be around two kilometres
    assertThat(distanceBetweenLocations).isGreaterThan(2.0).isLessThan(2.5);
  }
}
