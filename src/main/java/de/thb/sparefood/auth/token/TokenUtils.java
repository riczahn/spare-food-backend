package de.thb.sparefood.auth.token;

import de.thb.sparefood.auth.model.BasicAuthDTO;
import de.thb.sparefood.user.exception.UnknownUserException;
import de.thb.sparefood.user.service.UserService;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;

@ApplicationScoped
public class TokenUtils {

  @Inject UserService userService;

  public TokenUtils(UserService userService) {
    this.userService = userService;
  }

  public String generateToken(BasicAuthDTO basicAuthDTO) throws UnknownUserException {
    if (!userService.isCorrectPasswordProvided(basicAuthDTO)) {
      throw new SecurityException("Wrong combination of username and password");
    }

    return Jwt.issuer("https://localhost/sparefood")
        .upn(basicAuthDTO.getEmail())
        .expiresAt(Instant.now().plusSeconds(60000))
        .groups(new HashSet<>(Collections.singletonList("User")))
        .claim(Claims.birthdate.name(), "1999-08-12")
        .sign();
  }
}
