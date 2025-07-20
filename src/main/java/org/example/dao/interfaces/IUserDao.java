package org.example.dao.interfaces;

import org.example.model.User;
import java.util.List;

public interface IUserDao {
    User create(User user);
    User findById(Long id);
    List<User> findAll();
    User update(User user);
    boolean delete(Long id);
    void assignRolesToUser(Long userId, List<Long> roleIds);
}