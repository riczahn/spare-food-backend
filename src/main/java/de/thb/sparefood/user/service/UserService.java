package de.thb.sparefood.user.service;

import de.thb.sparefood.user.model.User;
import de.thb.sparefood.user.repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UserService {
  @Inject UserRepository userRepository;

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
}
