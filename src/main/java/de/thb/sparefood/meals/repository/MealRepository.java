package de.thb.sparefood.meals.repository;

import de.thb.sparefood.meals.model.Meal;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MealRepository implements PanacheRepository<Meal> {}
