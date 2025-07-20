package org.example.service.impl;

import org.example.dao.interfaces.IPermissionDao;
import org.example.model.Permission;
import org.example.service.interfaces.IPermissionService;
import java.util.List;

public class PermissionServiceImpl implements IPermissionService {
    private final IPermissionDao permissionDao;

    public PermissionServiceImpl(IPermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    @Override
    public Permission createPermission(Permission permission) {
        if (permission == null || permission.getName() == null || permission.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Permission name cannot be null or empty");
        }
        return permissionDao.create(permission);
    }

    @Override
    public Permission getPermissionById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid permission ID");
        }
        return permissionDao.findById(id);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionDao.findAll();
    }

    @Override
    public Permission updatePermission(Permission permission) {
        if (permission == null || permission.getId() == null || permission.getId() <= 0) {
            throw new IllegalArgumentException("Invalid permission data");
        }
        return permissionDao.update(permission);
    }

    @Override
    public boolean deletePermission(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid permission ID");
        }
        return permissionDao.delete(id);
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("Invalid role ID");
        }
        return permissionDao.findPermissionsByRoleId(roleId);
    }
}
