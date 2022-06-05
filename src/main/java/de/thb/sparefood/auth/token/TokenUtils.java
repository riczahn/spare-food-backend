package de.thb.sparefood.auth.token;

import de.thb.sparefood.auth.model.BasicAuthDTO;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;

@ApplicationScoped
public class TokenUtils {

  public String generateToken(BasicAuthDTO basicAuthDTO) {
    if (!isBasicAuthSuccessful(basicAuthDTO.getEmail(), basicAuthDTO.getPassword())) {
      throw new SecurityException("Wrong combination of username and password");
    }

    return Jwt.issuer("https://localhost/sparefood")
        .upn(basicAuthDTO.getEmail())
        .expiresAt(Instant.now().plusSeconds(60000))
        .groups(new HashSet<>(Collections.singletonList("User")))
        .claim(Claims.birthdate.name(), "1999-08-12")
        .sign();
  }

  private boolean isBasicAuthSuccessful(String email, String password) {
    // this method needs to be replaced with a database call for a real check
    if ("trompell@th-brandenburg.de".equals(email)) {
      return "123".equals(password);
    }

    if ("luedrick@th-brandenburg.de".equals(email)) {
      return "1234".equals(password);
    }

    if ("zahn@th-brandenburg.de".equals(email)) {
      return "12345".equals(password);
    }

    return false;
  }
}
