package com.optum.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optum.dao.PermissionDao;
import com.optum.entity.Permission;

@Service
public class PermissionService {

    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private PermissionDao permissionRepository;

    @Value("${permissions}")
    private String permissionNames;
    
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    
    @Transactional
    public void addPermissionNames() {
        List<String> names = Arrays.asList(permissionNames.split(","));
        for (String name : names) {
            if (!isPermissionExists(name.trim())) {
                Permission permission = new Permission();
                permission.setPermissionName(name);
                entityManager.persist(permission);
            }
        }
    }

    public boolean isPermissionExists(String permissionName) {
        String query = "SELECT COUNT(p) FROM Permission p WHERE p.name = :name";
        Long count = (Long) entityManager.createQuery(query)
                .setParameter("name", permissionName)
                .getSingleResult();
        return count > 0;
    }
}

