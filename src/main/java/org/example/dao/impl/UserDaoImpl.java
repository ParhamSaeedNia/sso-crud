package org.example.dao.impl;

import org.example.dao.interfaces.IUserDao;
import org.example.model.User;
import org.example.utils.ConnectionPool;
import org.example.utils.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements IUserDao {
    @Override
    public User create(User user) {
        String sql = DBUtil.getQuery("user.create");

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID", e);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        String sql = DBUtil.getQuery("user.findAll");
        List<User> users = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all users", e);
        }
        return users;
    }

    @Override
    public User update(User user) {
        String sql = DBUtil.getQuery("user.update");

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setLong(2, user.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = DBUtil.getQuery("user.delete");

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        try {
            if (!userExists(userId)) {
                throw new RuntimeException("User with ID " + userId + " does not exist");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Long roleId : roleIds) {
            try {
                if (!roleExists(roleId)) {
                    throw new RuntimeException("Role with ID " + roleId + " does not exist");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        String sql = DBUtil.getQuery("user.assignRoles");
        System.out.println("Using SQL: " + sql); // Debug log

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            conn.setAutoCommit(false);

            // Debug log the clear operation
            String clearSql = DBUtil.getQuery("user.clearRoles");
            System.out.println("Clearing roles with SQL: " + clearSql);

            try (PreparedStatement clearStmt = conn.prepareStatement(clearSql)) {
                clearStmt.setLong(1, userId);
                int cleared = clearStmt.executeUpdate();
                System.out.println("Cleared " + cleared + " existing role assignments");
            }

            // Debug log the batch operation
            System.out.println("Assigning " + roleIds.size() + " roles");
            try (PreparedStatement assignStmt = conn.prepareStatement(sql)) {
                for (Long roleId : roleIds) {
                    assignStmt.setLong(1, userId);
                    assignStmt.setLong(2, roleId);
                    assignStmt.addBatch();
                }
                int[] results = assignStmt.executeBatch();
                System.out.println("Batch executed, " + results.length + " rows affected");
            }

            conn.commit();
            System.out.println("Transaction committed successfully");

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                }
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
            throw new RuntimeException("Error assigning roles to user", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    private boolean roleExists(Long roleId) throws SQLException {
        String sql = "SELECT 1 FROM roles WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    private boolean userExists(Long userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
