package org.example.dao.impl;

import org.example.dao.interfaces.IPermissionDao;
import org.example.model.Permission;
import org.example.utils.ConnectionPool;
import org.example.utils.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDaoImpl implements IPermissionDao {

    @Override
    public Permission create(Permission permission) {
        String sql = DBUtil.getQuery("permission.create");

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, permission.getName());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating permission failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    permission.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating permission failed, no ID obtained.");
                }
            }
            return permission;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating permission", e);
        }
    }

    @Override
    public Permission findById(Long id) {
        String sql = DBUtil.getQuery("permission.findById");
        Permission permission = null;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    permission = new Permission();
                    permission.setId(rs.getLong("id"));
                    permission.setName(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding permission by ID", e);
        }
        return permission;
    }

    @Override
    public List<Permission> findAll() {
        String sql = DBUtil.getQuery("permission.findAll");
        List<Permission> permissions = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getLong("id"));
                permission.setName(rs.getString("name"));
                permissions.add(permission);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all permissions", e);
        }
        return permissions;
    }

    @Override
    public Permission update(Permission permission) {
        String sql = DBUtil.getQuery("permission.update");

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, permission.getName());
            ps.setLong(2, permission.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating permission failed, no rows affected.");
            }
            return permission;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating permission", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = DBUtil.getQuery("permission.delete");

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting permission", e);
        }
    }

    @Override
    public List<Permission> findPermissionsByRoleId(Long roleId) {
        String sql = DBUtil.getQuery("permission.findByRoleId");
        List<Permission> permissions = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Permission permission = new Permission();
                    permission.setId(rs.getLong("id"));
                    permission.setName(rs.getString("name"));
                    permissions.add(permission);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding permissions by role ID", e);
        }
        return permissions;
    }
}