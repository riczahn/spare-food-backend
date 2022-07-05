package de.thb.sparefood.meals.repository;

import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.model.Property;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class MealRepository implements PanacheRepository<Meal> {
  public List<Meal> findAllMealsWithProperties(List<Property> filterCriteria) {
    StringBuilder query = new StringBuilder("SELECT m.* FROM meal m");

    int i = 0;
    for (Property property : filterCriteria) {
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

    return getEntityManager().createNativeQuery(query.toString(), Meal.class).getResultList();
  }
}
