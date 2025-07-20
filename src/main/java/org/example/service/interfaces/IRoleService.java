package org.example.service.interfaces;

import org.example.model.Role;
import java.util.List;

public interface IRoleService {
    Role createRole(Role role);
    Role getRoleById(Long id);
    List<Role> getAllRoles();
    Role updateRole(Role role);
    boolean deleteRole(Long id);
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}