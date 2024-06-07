package com.optum.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.optum.dao.RoleDao;
import com.optum.dto.RoleDTO;
import com.optum.dto.RoleInfo;
import com.optum.entity.Role;

@Service
public class RoleService {

    @Autowired
    private RoleDao roleRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${roles}")
    private String roleNames;

    public Role createNewRole(Role role) {
    	// Extract roleName from the incoming Role object
        String roleName = role.getRoleName();

        // Set the roleName to the incoming Role object (optional)
        role.setRoleName(roleName);

        return roleRepository.save(role);
    }

    public void updateRole(Integer id, Role role) {
        Role existingRole = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        existingRole.setRoleName(role.getRoleName());
        // Update other properties if needed
        roleRepository.save(existingRole);
    }

    public void deleteRole(Integer id) {
        Role existingRole = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        roleRepository.delete(existingRole);
    }
    
    @Transactional
    public List<RoleInfo> getAllRoles() {
        return roleRepository.findAll().stream()
                             .map(role -> new RoleInfo(role.getRoleRid(), role.getRoleName()))
                             .collect(Collectors.toList());
    }
    
    public static String extractRoleNames(Set<RoleDTO> roles) {
        return roles.stream()
                    .map(RoleDTO::getRoleName) // Adjust this method call to match your RoleDTO
                    .collect(Collectors.joining(", "));
    }
    
    @Transactional
    public void addRoleNames() {
        List<String> names = Arrays.asList(roleNames.split(","));
        for (String name : names) {
            if (!isRoleExists(name.trim())) {
                Role role = new Role();
                role.setRoleName(name);
                entityManager.persist(role);
            }
        }
    }

    public boolean isRoleExists(String roleName) {
        String query = "SELECT COUNT(r) FROM Role r WHERE r.name = :name";
        Long count = (Long) entityManager.createQuery(query)
                .setParameter("name", roleName)
                .getSingleResult();
        return count > 0;
    }
}
