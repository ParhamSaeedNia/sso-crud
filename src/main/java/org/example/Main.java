package org.example;

import org.example.dao.impl.PermissionDaoImpl;
import org.example.dao.impl.RoleDaoImpl;
import org.example.dao.impl.UserDaoImpl;
import org.example.model.Permission;
import org.example.model.Role;
import org.example.model.User;
import org.example.service.impl.PermissionServiceImpl;
import org.example.service.impl.RoleServiceImpl;
import org.example.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Create DAO instances
        UserDaoImpl userDao = new UserDaoImpl();
        RoleDaoImpl roleDao = new RoleDaoImpl();
        PermissionDaoImpl permissionDao = new PermissionDaoImpl();

        UserServiceImpl userService = new UserServiceImpl(userDao, roleDao);
        RoleServiceImpl roleService = new RoleServiceImpl(roleDao);
        PermissionServiceImpl permissionService = new PermissionServiceImpl(permissionDao);

        createTestData(userService, roleService, permissionService);

        try {
            List<Long> permissionIds = permissionService.getAllPermissions().stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());

            userService.assignRolesToUser(1L, Arrays.asList(1L, 2L));
            roleService.assignPermissionsToRole(1L, permissionIds);
            System.out.println("All operations completed successfully!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTestData(UserServiceImpl userService, RoleServiceImpl roleService, PermissionServiceImpl permissionService) {
        try {
            User user = new User();
            user.setUsername("testuser");
            user = userService.createUser(user);
            System.out.println("Created user with ID: " + user.getId());

            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole = roleService.createRole(adminRole);
            System.out.println("Created ADMIN role with ID: " + adminRole.getId());

            Role userRole = new Role();
            userRole.setName("USER");
            userRole = roleService.createRole(userRole);
            System.out.println("Created USER role with ID: " + userRole.getId());

            Permission createPerm = new Permission();
            createPerm.setName("CREATE");
            createPerm = permissionService.createPermission(createPerm);
            System.out.println("Created CREATE permission with ID: " + createPerm.getId());

            Permission readPerm = new Permission();
            readPerm.setName("READ");
            readPerm = permissionService.createPermission(readPerm);
            System.out.println("Created READ permission with ID: " + readPerm.getId());

        } catch (Exception e) {
            System.err.println("Failed to create test data: " + e.getMessage());
            throw e;
        }
    }

}