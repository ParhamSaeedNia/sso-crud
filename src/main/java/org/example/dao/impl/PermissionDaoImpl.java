package org.example.dao.impl;

import org.example.dao.interfaces.IPermissionDao;
import org.example.model.Permission;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;

public class PermissionDaoImpl implements IPermissionDao {

    @Override
    public Permission create(Permission permission) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(permission);
            transaction.commit();
            return permission;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error creating permission", e);
        }
    }

    @Override
    public Permission findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Permission.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding permission by ID", e);
        }
    }

    @Override
    public List<Permission> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Permission> criteria = builder.createQuery(Permission.class);
            criteria.from(Permission.class);
            return session.createQuery(criteria).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all permissions", e);
        }
    }

    @Override
    public Permission update(Permission permission) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Permission updatedPermission = session.merge(permission);
            transaction.commit();
            return updatedPermission;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating permission", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Permission permission = session.get(Permission.class, id);
            if (permission != null) {
                session.remove(permission);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting permission", e);
        }
    }

    @Override
    public List<Permission> findPermissionsByRoleId(Long roleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT p FROM Permission p " +
                        "JOIN RolePermission rp ON p.id = rp.permission.id " +
                        "WHERE rp.role.id = :roleId";
            return session.createQuery(hql, Permission.class)
                    .setParameter("roleId", roleId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding permissions by role ID", e);
        }
    }
}