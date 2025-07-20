package org.example.dao.interfaces;

import org.example.model.Permission;
import java.util.List;

public interface IPermissionDao {
    Permission create(Permission permission);
    Permission findById(Long id);
    List<Permission> findAll();
    Permission update(Permission permission);
    boolean delete(Long id);

    List<Permission> findPermissionsByRoleId(Long roleId);
}