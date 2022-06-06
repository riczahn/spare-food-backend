package de.thb.sparefood.user.service;

import de.thb.sparefood.auth.model.BasicAuthDTO;
import de.thb.sparefood.auth.service.AuthenticationHelper;
import de.thb.sparefood.user.exception.UnknownUserException;
import de.thb.sparefood.user.model.User;
import de.thb.sparefood.user.repository.UserRepository;
import lombok.AllArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@AllArgsConstructor
public class UserService {

  @Inject UserRepository userRepository;
  @Inject AuthenticationHelper authenticationHelper;

  public List<User> getAllUsers() {
    return userRepository.listAll();
  }

  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional
  public User addUser(User user) {
    if (user.getEmail() == null) {
      throw new InvalidParameterException("Missing value for property email");
    }

    userRepository.persist(user);
    return user;
  }

  @Transactional
  public void removeUserWithEmail(String email) throws UnknownUserException {
    userRepository.deleteByEmail(email);
  }

  public boolean isCorrectPasswordProvided(BasicAuthDTO basicAuthDTO) throws UnknownUserException {
    Optional<User> user = getUserByEmail(basicAuthDTO.getEmail());

    if (user.isEmpty()) {
      throw new UnknownUserException(
          String.format("No user found with email %s", basicAuthDTO.getEmail()));
    }

    return authenticationHelper.isPasswordCorrect(
        user.get().getPassword(), basicAuthDTO.getPassword());
  }
}
