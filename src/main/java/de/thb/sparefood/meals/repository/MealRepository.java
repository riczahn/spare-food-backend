package de.thb.sparefood.meals.repository;

import de.thb.sparefood.meals.model.FilterCriteria;
import de.thb.sparefood.meals.model.Location;
import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.model.Property;
import de.thb.sparefood.meals.service.LocationDistanceService;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MealRepository implements PanacheRepository<Meal> {

  @Inject LocationDistanceService locationDistanceService;

  public List<Meal> findAllMealsWithProperties(FilterCriteria filterCriteria) {
    StringBuilder query = new StringBuilder("SELECT m.* FROM meal m");

    int i = 0;
    for (Property property : filterCriteria.getProperties()) {
      String subSelectId = "m" + i;
      query
          .append(" INNER JOIN (SELECT meal_id FROM meal_properties WHERE properties='")
          .append(property.name())
          .append("') ")
          .append(subSelectId)
          .append(" ON m.id = ")
          .append(subSelectId)
          .append(".meal_id");

      i++;
    }

    query.append(" WHERE m.reservinguser_email IS NULL");

    List<Meal> allMealsWithRequiredProperties =
        getEntityManager().createNativeQuery(query.toString(), Meal.class).getResultList();

    return filterOutMealsOutOfRange(
        allMealsWithRequiredProperties,
        filterCriteria.getUserLocation(),
        filterCriteria.getSearchRadius());
  }

  private List<Meal> filterOutMealsOutOfRange(
      List<Meal> meals, Location location, double searchRadius) {
    if (location == null) {
      return meals;
    }

    List<Meal> mealsInRange = new ArrayList<>();

    for (Meal meal : meals) {
      if (isMealInRange(meal.getLocation(), location, searchRadius)) {
        mealsInRange.add(meal);
      }
    }

    return mealsInRange;
  }

  private boolean isMealInRange(
      Location locationOfMeal, Location locationOfUser, double searchRadius) {
    double distanceBetweenLocations =
        locationDistanceService.getDistanceBetweenLocations(locationOfMeal, locationOfUser);

    return distanceBetweenLocations <= searchRadius;
  }
}
