package org.example.service.impl;

import org.example.dao.impl.UserDaoImpl;
import org.example.dao.interfaces.IRoleDao;
import org.example.dao.interfaces.IUserDao;
import org.example.model.User;
import org.example.service.interfaces.IUserService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserServiceImpl implements IUserService {
    private final IUserDao userDao;
    private final IRoleDao roleDao;


    public UserServiceImpl(IUserDao userDao, IRoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }


    @Override
    public User createUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return userDao.create(user);
    }

    @Override
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userDao.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(User user) {
        if (user == null || user.getId() == null || user.getId() <= 0) {
            throw new IllegalArgumentException("Invalid user data");
        }
        return userDao.update(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userDao.delete(id);
    }

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // Validate input parameters
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }

        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("Role IDs list cannot be null or empty");
        }

        List<Long> validRoleIds = roleIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("User with ID " + userId + " not found");
        }

        for (Long roleId : validRoleIds) {
            if (roleDao.findById(roleId) == null) {
                throw new RuntimeException("Role with ID " + roleId + " does not exist");
            }
        }

        try {
            userDao.assignRolesToUser(userId, validRoleIds);
            System.out.println("Successfully assigned roles " + validRoleIds + " to user " + userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign roles to user", e);
        }
    }
}