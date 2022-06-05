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
import java.util.List;

@ApplicationScoped
@AllArgsConstructor
public class UserService {

  @Inject UserRepository userRepository;
  @Inject AuthenticationHelper authenticationHelper;

  public List<User> getAllUsers() {
    return userRepository.listAll();
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional
  public User addUser(User user) {
    userRepository.persist(user);
    return user;
  }

  @Transactional
  public void removeUserWithEmail(String email) {
    userRepository.deleteByEmail(email);
  }

  public boolean isCorrectPasswordProvided(BasicAuthDTO basicAuthDTO) throws UnknownUserException {
    User user = getUserByEmail(basicAuthDTO.getEmail());

    if (user == null) {
      throw new UnknownUserException(
          String.format("No user found with email %s", basicAuthDTO.getEmail()));
    }

    return authenticationHelper.isPasswordCorrect(user.getPassword(), basicAuthDTO.getPassword());
  }
}
