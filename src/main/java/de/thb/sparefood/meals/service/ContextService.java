package de.thb.sparefood.meals.service;

import de.thb.sparefood.user.model.User;
import de.thb.sparefood.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

@RequestScoped
public class ContextService {

  @Inject UserService userService;
  private static final Logger logger = LoggerFactory.getLogger(ContextService.class);

  public User getCurrentUser(SecurityContext ctx) throws UserPrincipalNotFoundException {
    String email = ctx.getUserPrincipal().getName();
    Optional<User> user = userService.getUserByEmail(email);

    if (user.isEmpty()) {
      logger.error("Couldn't find user for user principal name of {}", email);
      throw new UserPrincipalNotFoundException(
          "Couldn't find user for user principal name of " + email);
    }

    return user.get();
  }
}
