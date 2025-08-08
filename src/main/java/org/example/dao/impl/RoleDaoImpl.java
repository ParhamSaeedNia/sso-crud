package org.example.dao.impl;

import org.example.dao.interfaces.IRoleDao;
import org.example.model.Role;
import org.example.utils.ConnectionPool;
import org.example.utils.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements IRoleDao {

    @Override
    public Role create(Role role) {
        String sql = DBUtil.getQuery("role.create");

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, role.getName());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating role failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    role.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating role failed, no ID obtained.");
                }
            }
            return role;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating role", e);
        }
    }

    @Override
    public Role findById(Long id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getLong("id"));
                    role.setName(rs.getString("name"));
                    return role;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding role by ID", e);
        }
        return null;
    }

    @Override
    public List<Role> findAll() {
        String sql = DBUtil.getQuery("role.findAll");
        List<Role> roles = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getLong("id"));
                role.setName(rs.getString("name"));
                roles.add(role);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all roles", e);
        }
        return roles;
    }

    @Override
    public Role update(Role role) {
        String sql = DBUtil.getQuery("role.update");

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role.getName());
            ps.setLong(2, role.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating role failed, no rows affected.");
            }
            return role;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating role", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = DBUtil.getQuery("role.delete");

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting role", e);
        }
    }

    @Override
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) return;

        String sql = DBUtil.getQuery("role.assignPermissions");
        Connection conn = null;

        try {
            conn = ConnectionPool.getConnection();
            conn.setAutoCommit(false);

            // Clear existing permissions first
            try (PreparedStatement clearStmt = conn.prepareStatement(
                    DBUtil.getQuery("role.clearPermissions"))) {
                clearStmt.setLong(1, roleId);
                clearStmt.executeUpdate();
            }

            // Assign new permissions
            try (PreparedStatement assignStmt = conn.prepareStatement(sql)) {
                for (Long permissionId : permissionIds) {
                    assignStmt.setLong(1, roleId);
                    assignStmt.setLong(2, permissionId);
                    assignStmt.addBatch();
                }
                assignStmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Error assigning permissions to role", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}