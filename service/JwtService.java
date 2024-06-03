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
import org.springframework.transaction.annotation.Transactional;

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
    private UserDao userDao;
    

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Transactional
    public JwtResponse createJwtToken(JwtRequest jwtRequest) throws BadCredentialsException, UsernameNotFoundException {
        String userName = jwtRequest.getUserName();
        String userPassword = jwtRequest.getUserPassword();
        authenticate(userName, userPassword);

        UserDetails userDetails = loadUserByUsername(userName);
        String newGeneratedToken = jwtUtil.generateToken(userDetails);

        User user = userDao.findByUserNameWithRolesAndPermissions(userName)
                           .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<RoleInfo> roleInfos = new HashSet<>();
        for (UserRole userRole : user.getUserRoles()) {
            Role role = userRole.getRole();
            RoleInfo roleInfo = new RoleInfo(role.getRoleRid(), role.getRoleName());

            Set<PermissionInfo> permissionInfos = role.getRolePermissions().stream()
                                                       .map(rolePermission -> {
                                                           Permission permission = rolePermission.getPermission();
                                                           return new PermissionInfo(permission.getPermissionRid(), permission.getPermissionName());
                                                       })
                                                       .collect(Collectors.toSet());

            roleInfo.setPermissions(permissionInfos);
            roleInfos.add(roleInfo);
        }

        return new JwtResponse(201, null, "Successfully Logged In", newGeneratedToken, roleInfos, user);
    }


//    public JwtResponse createJwtToken(JwtRequest jwtRequest) throws BadCredentialsException, UsernameNotFoundException {
//        String userName = jwtRequest.getUserName();
//        String userPassword = jwtRequest.getUserPassword();
//        authenticate(userName, userPassword);
//
//        UserDetails userDetails = loadUserByUsername(userName);
//        String newGeneratedToken = jwtUtil.generateToken(userDetails);
//
//        User user = userDao.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        Set<RoleInfo> roleInfos = new HashSet<>();
//        user.getUserRoles().size();
//
//        for (UserRole userRole : user.getUserRoles()) {
//            Role role = userRole.getRole();
//            RoleInfo roleInfo = new RoleInfo();
//            roleInfo.setRoleRid(role.getRoleRid());
//            roleInfo.setRoleName(role.getRoleName());
//
//            Set<PermissionInfo> permissionInfos = new HashSet<>();
//            role.getRolePermissions().size();
//
//            for (RolePermission rolePermission : role.getRolePermissions()) {
//                Permission permission = rolePermission.getPermission();
//                PermissionInfo permissionInfo = new PermissionInfo();
//                permissionInfo.setPermissionRid(permission.getPermissionRid());
//                permissionInfo.setPermissionName(permission.getPermissionName());
//                permissionInfos.add(permissionInfo);
//            }
//            roleInfo.setPermissions(permissionInfos);
//            roleInfos.add(roleInfo);
//        }
//
//        return new JwtResponse(201, null, "Successfully Logged In", newGeneratedToken, roleInfos, user);
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getUserPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
        });
        return authorities;
    }

    private void authenticate(String userName, String userPassword) throws BadCredentialsException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, userPassword));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }
}
