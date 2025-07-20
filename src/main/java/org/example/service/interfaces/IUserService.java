package org.example.service.interfaces;

import org.example.model.User;
import java.util.List;

public interface IUserService {
    User createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(User user);
    boolean deleteUser(Long id);
    void assignRolesToUser(Long userId, List<Long> roleIds);
}