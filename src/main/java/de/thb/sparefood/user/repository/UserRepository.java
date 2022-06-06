package de.thb.sparefood.user.repository;

import de.thb.sparefood.user.exception.UnknownUserException;
import de.thb.sparefood.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

  public Optional<User> findByEmail(String email) {
    return find("email", email).firstResultOptional();
  }

  public void deleteByEmail(String email) throws UnknownUserException {
    Optional<User> user = find("email", email).firstResultOptional(); // email is unique

    if (user.isEmpty()) {
      throw new UnknownUserException("No user found with email %s".formatted(email));
    }
      delete(user.get());
  }
}
