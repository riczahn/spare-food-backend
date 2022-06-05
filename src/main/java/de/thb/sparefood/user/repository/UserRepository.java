package de.thb.sparefood.user.repository;

import de.thb.sparefood.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

  public Optional<User> findByEmail(String email) {
    return find("email", email).firstResultOptional();
  }

  public void deleteByEmail(String email) {
    User user = find("email", email).firstResult(); // email is unique
    delete(user);
  }
}
