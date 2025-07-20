package org.example.service.interfaces;

import org.example.model.Permission;

import java.util.List;

public interface IPermissionService {
    Permission createPermission(Permission permission);
    Permission getPermissionById(Long id);
    List<Permission> getAllPermissions();
    Permission updatePermission(Permission permission);
    boolean deletePermission(Long id);
    List<Permission> getPermissionsByRoleId(Long roleId);
}
