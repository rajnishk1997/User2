package com.optum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.optum.dao.PermissionDao;
import com.optum.dao.RolePermissionDao;
import com.optum.dao.UserDao;
import com.optum.dto.response.PermissionInfo;
import com.optum.dto.response.RoleInfo;
import com.optum.entity.JwtRequest;
import com.optum.entity.JwtResponse;
import com.optum.entity.Permission;
import com.optum.entity.Role;
import com.optum.entity.RolePermission;
import com.optum.entity.User;
import com.optum.entity.UserRole;
import com.optum.util.JwtUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService implements UserDetailsService {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RolePermissionDao rolePermissionRepository;

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PermissionDao permissionRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtResponse createJwtToken(JwtRequest jwtRequest) {
        try {
            String userName = jwtRequest.getUserName();
            String userPassword = jwtRequest.getUserPassword();
            try {
                authenticate(userName, userPassword);
            } catch (BadCredentialsException e) {
                return new JwtResponse(401, "Unauthorized", "Invalid Credentials", null, null, null);
            }

            UserDetails userDetails = loadUserByUsername(userName);
            String newGeneratedToken = jwtUtil.generateToken(userDetails);
            
            User user = userDao.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));

            // Initialize sets for role infos and permission infos
            Set<RoleInfo> roleInfos = new HashSet<>();

            // Eagerly fetch user roles along with user data
            user.getUserRoles().size();
            
            // Iterate over user's roles
            for (UserRole userRole : user.getUserRoles()) {
                Role role = userRole.getRole();

                // Initialize role info
                RoleInfo roleInfo = new RoleInfo();
                roleInfo.setRoleRid(role.getRoleRid());
                roleInfo.setRoleName(role.getRoleName());

                // Initialize set for permission infos
                Set<PermissionInfo> permissionInfos = new HashSet<>();

                // Eagerly fetch role permissions along with role data
                role.getRolePermissions().size();

                // Iterate over role's permissions
                for (RolePermission rolePermission : role.getRolePermissions()) {
                    Permission permission = rolePermission.getPermission();

                    // Initialize permission info
                    PermissionInfo permissionInfo = new PermissionInfo();
                    permissionInfo.setPermissionRid(permission.getPermissionRid());
                    permissionInfo.setPermissionName(permission.getPermissionName());

                    // Add permission info to set
                    permissionInfos.add(permissionInfo);
                }

                // Set permissions for role
                roleInfo.setPermissions(permissionInfos);

                // Add role info to set
                roleInfos.add(roleInfo);
            }

            return new JwtResponse(201, null, "Successfully Logged In", newGeneratedToken, roleInfos, user);
        } catch (BadCredentialsException e) {
            return new JwtResponse(401, "Unauthorized", "Invalid Credentials", null, null, null);
        } catch (Exception e) {
            return new JwtResponse(500, "Internal Server Error", "Something went wrong", null, null, null);
        }
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUserName(username).get();

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getUserName(),
                    user.getUserPassword(),
                    getAuthority(user)
            );
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    private Set getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
        });
        return authorities;
    }

    private void authenticate(String userName, String userPassword) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, userPassword));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
