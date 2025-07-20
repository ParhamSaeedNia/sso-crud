package org.example.service.impl;

import org.example.dao.interfaces.IRoleDao;
import org.example.model.Role;
import org.example.service.interfaces.IRoleService;
import java.util.List;

public class RoleServiceImpl implements IRoleService {
    private final IRoleDao roleDao;

    public RoleServiceImpl(IRoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public Role createRole(Role role) {
        if (role == null || role.getName() == null || role.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
        return roleDao.create(role);
    }

    @Override
    public Role getRoleById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid role ID");
        }
        return roleDao.findById(id);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDao.findAll();
    }

    @Override
    public Role updateRole(Role role) {
        if (role == null || role.getId() == null || role.getId() <= 0) {
            throw new IllegalArgumentException("Invalid role data");
        }
        return roleDao.update(role);
    }

    @Override
    public boolean deleteRole(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid role ID");
        }
        return roleDao.delete(id);
    }

    @Override
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("Role ID must be a positive number");
        }

        if (permissionIds == null) {
            throw new IllegalArgumentException("Permission IDs list cannot be null");
        }
         roleDao.assignPermissionsToRole(roleId,permissionIds);
    }
}