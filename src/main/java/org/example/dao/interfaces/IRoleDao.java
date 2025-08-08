package org.example.dao.interfaces;

import org.example.model.Role;
import java.util.List;

public interface IRoleDao {
    Role create(Role role);
    Role findById(Long id);
    List<Role> findAll();
    Role update(Role role);
    boolean delete(Long id);
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}