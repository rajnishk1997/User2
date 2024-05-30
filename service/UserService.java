package com.optum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.optum.dao.PermissionDao;
import com.optum.dao.ReqRes;
import com.optum.dao.RoleDao;
import com.optum.dao.RolePermissionDao;
import com.optum.dao.UserDao;
import com.optum.dao.UserRoleDao;
import com.optum.dto.PermissionDTO;
import com.optum.dto.RoleDTO;
import com.optum.dto.UserDTO;
import com.optum.dto.UserInfo;
import com.optum.dto.request.UserRequestDTO;
import com.optum.entity.JwtResponse;
import com.optum.entity.Permission;
import com.optum.entity.RegistrationResponse;
import com.optum.entity.Role;
import com.optum.entity.RolePermission;
import com.optum.entity.User;
import com.optum.entity.UserRole;
import com.optum.exception.UserRegistrationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import com.optum.dto.RoleDTO;

import java.util.Optional;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;

	private static final Logger logger = LogManager.getLogger(UserService.class);

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PermissionDao permissionRepository;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private RolePermissionDao rolePermissionDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

//    @Transactional
//    public void initPermissions() {
//		 List<String> permissionNames = Arrays.asList(permissionsName.split(", "));
//	        for (String permissionName : permissionNames) {
//	            Permission permission = permissionRepository.findByPermissionName(permissionName);
//	            if (permission == null) {
//	                permission = new Permission(permissionName);
//	                permissionRepository.save(permission);
//	            }
//	        }
//		
//	}

	@Transactional
	public void initRoleAndUser() {
		// Role 1- Admin
		Role adminRole = new Role();
		adminRole.setRoleName("Admin");
		adminRole.setRoleDescription("Admin role");
		roleDao.save(adminRole);

		// Role 2- Manager
		Role managerRole = new Role();
		managerRole.setRoleName("Manager");
		managerRole.setRoleDescription("Manager role");
		roleDao.save(managerRole);

		// Role 3- Analyst
		Role analystRole = new Role();
		analystRole.setRoleName("Analyst");
		analystRole.setRoleDescription("Analyst role");
		roleDao.save(analystRole);

		// Role 4- Auditor
		Role auditorRole = new Role();
		auditorRole.setRoleName("Auditor");
		auditorRole.setRoleDescription("Auditor role");
		roleDao.save(auditorRole);

		// Adding Permissions with Roles:
		// For adminRole, fetch permissions from the database and associate them
		List<Permission> allPermissions = permissionRepository.findAll();
		adminRole.setPermissions(new HashSet<>(allPermissions));

		// For managerRole, create a list of specific permission names
		List<String> permissionNames = Arrays.asList("NEW USER MANAGEMENT", "NETWORK TICKETS", "OTHER");

		List<Permission> managerPermissions = new ArrayList<>();
		for (String permissionName : permissionNames) {
			Permission permission = permissionRepository.findByPermissionName(permissionName);
			if (permission != null) {
				managerPermissions.add(permission);
			} else {
				// Permission doesn't exist, create it and add it to the database
				permission = new Permission();
				permission.setPermissionName(permissionName);
				// You can set other properties of the permission if needed
				// Save the permission to the database
				permissionRepository.save(permission);

				// Add the permission to the list of managerPermissions
				managerPermissions.add(permission);
			}
		}

		// Set permissions for the manager role
		managerRole.setPermissions(new HashSet<>(managerPermissions));

		// For auditorRole and analystRole, don't associate any permissions
		auditorRole.setPermissions(new HashSet<>());
		analystRole.setPermissions(new HashSet<>());

		// Save all roles to the database
		roleDao.save(adminRole);
		roleDao.save(managerRole);
		roleDao.save(auditorRole);
		roleDao.save(analystRole);

		// User 1 - Admin-User
		User adminUser = new User();
		adminUser.setUserName("admin");
		adminUser.setUserFirstName("admin1");
		adminUser.setUserPassword(getEncodedPassword("admin"));
		adminUser.setUserLastName("Sharma");
		adminUser.setUserEmail("admin@gmail.com");
		adminUser.setUserEmployeeId("E1941945");
		adminUser.setNewUser(false);
		adminUser.setCreatedDate(new Date());
		adminUser.setModifiedDate(new Date());
		Set<Role> adminRoles = new HashSet<>();
		adminRoles.add(adminRole);
		adminUser.setRoles(adminRoles);
		userDao.save(adminUser);

		// User 2 - User with Manager Role
		User managerRoleUser = new User();
		managerRoleUser.setUserName("Rajesh");
		managerRoleUser.setUserFirstName("Rajesh");
		managerRoleUser.setUserPassword(getEncodedPassword("admin"));
		managerRoleUser.setUserLastName("Sharma");
		managerRoleUser.setUserEmail("manager@gmail.com");
		managerRoleUser.setUserEmployeeId("E1941943");
		managerRoleUser.setCreatedDate(new Date());
		managerRoleUser.setModifiedDate(new Date());
		Set<Role> managerRoles = new HashSet<>();
		managerRoles.add(managerRole);
		managerRoleUser.setRoles(managerRoles);
		userDao.save(managerRoleUser);
	}

	 @Transactional
	    public RegistrationResponse<User> registerNewUser(UserRequestDTO userRequestDTO) {
	        try {
	            // Log incoming request
	            logger.debug("Registering new user with details: {}", userRequestDTO);

	            // Check if user already exists by email
	            if (userDao.existsByUserEmail(userRequestDTO.getUserEmail())) {
	                throw new IllegalArgumentException("User already exists with email: " + userRequestDTO.getUserEmail());
	            }

	            // Retrieve existing roles and their permissions from the database
	            Set<Role> userRoles = fetchRoles(userRequestDTO.getRoles());

	            // Create User entity
	            User user = mapToUserEntity(userRequestDTO, userRoles);

	            // Encrypt the password
	            String plainPassword = generatePlainPassword(); 
	            user.setUserPassword(passwordEncoder.encode(plainPassword));
	            user.setUserPlainPassword(plainPassword);

	            // Save the user
	            User savedUser = userDao.save(user);

	            // Create and return the registration response
	            return new RegistrationResponse<>(HttpStatus.CREATED.value(), null, "Successfully Registered User",
	                    savedUser.getUserName(), savedUser.getUserPlainPassword());
	        } catch (IllegalArgumentException e) {
	            logger.error("Error registering new user: " + e.getMessage());
	            throw e;
	        } catch (Exception e) {
	            logger.error("An error occurred while registering the user", e);
	            throw new UserRegistrationException("An error occurred while registering the user", e);
	        }
	    }

	    private Set<Role> fetchRoles(Set<RoleDTO> roleDTOs) {
	        Set<String> roleNames = roleDTOs.stream()
	                                        .map(RoleDTO::getRoleName)
	                                        .collect(Collectors.toSet());
	        return new HashSet<>(roleDao.findByRoleNameIn(roleNames));
	    }

	    private User mapToUserEntity(UserRequestDTO dto, Set<Role> roles) {
	        User user = new User();
	        user.setUserFirstName(dto.getUserFirstName());
	        user.setUserLastName(dto.getUserLastName());
	        user.setUserEmail(dto.getUserEmail());
	        user.setCurrentUserId(dto.getCurrentUserId());
	        user.setRoles(roles);
	        user.setUserName(generateUserName(dto.getUserFirstName(), dto.getUserLastName()));
	        Date currentDate = new Date();
	        user.setCreatedDate(currentDate);
	        user.setCreatedBy(dto.getCurrentUserId());
	        user.setModifiedDate(currentDate);
	        user.setModifiedBy(dto.getCurrentUserId());
	        return user;
	    }

	public String generateUserName(String firstName, String lastName) {
		// Validate input parameters
		if (firstName == null || lastName == null || firstName.isEmpty() || lastName.isEmpty()) {
			throw new IllegalArgumentException("First name and last name cannot be null or empty.");
		}

		// Generate a random number between 1000 and 9999
		Random random = new Random();
		int randomNumber = random.nextInt(9000) + 1000; // To ensure it's a 4-digit number

		// Concatenate the first name, last name, and random number to form the username
		String userName = firstName.toLowerCase() + lastName.toLowerCase() + randomNumber;
		return userName;
	}

	String generateSystemPassword(String firstName, String lastName) {
		// Check if last name is null or empty
		if (lastName == null || lastName.isEmpty()) {
			throw new IllegalArgumentException("Last name cannot be null or empty.");
		}

		// Generate a random number between 0 and 9999
		Random random = new Random();
		int randomNumber = random.nextInt(1000);

		// Choose a random symbol from a predefined set
		String symbols = "!@#$%^&*()_-+=<>?/[]{},.";
		char randomSymbol = symbols.charAt(random.nextInt(symbols.length()));

		// Concatenate the parts to form the password
		String generatedPassword = firstName + lastName + randomNumber + randomSymbol;

		return generatedPassword;
	}

	public String getEncodedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	public String createFullName(String firstName, String middleName, String lastName) {
		StringBuilder fullName = new StringBuilder();

		if (firstName != null) {
			fullName.append(firstName);
		}

		if (middleName != null) {
			if (fullName.length() > 0) {
				fullName.append(" ");
			}
			fullName.append(middleName);
		}

		if (lastName != null) {
			if (fullName.length() > 0) {
				fullName.append(" ");
			}
			fullName.append(lastName);
		}

		return fullName.toString();
	}

	@Transactional
	public ReqRes updateUser(String userName, UserRequestDTO userRequestDTO) {
	    logger.info("Updating user with username: {}", userName);
	    Optional<User> optionalUser = userDao.findByUserName(userName);
	    if (!optionalUser.isPresent()) {
	        logger.warn("User not found with username: {}", userName);
	        return new ReqRes(404, "Not Found", "User not found");
	    }

	    User user = optionalUser.get();

	    // Update user fields
	    logger.info("Updating user fields for username: {}", userName);
	    user.setUserFirstName(userRequestDTO.getUserFirstName());
	    user.setUserLastName(userRequestDTO.getUserLastName());
	    user.setUserEmail(userRequestDTO.getUserEmail());

	    // Get new roles from the request
	    Set<Role> newRoles = new HashSet<>();
	    for (RoleDTO roleDTO : userRequestDTO.getRoles()) {
	        Role role = roleDao.findByRoleRid(roleDTO.getRoleRid())
	                .orElseThrow(() -> {
	                    logger.error("Role not found with roleRid: {}", roleDTO.getRoleRid());
	                    return new RuntimeException("Role not found: " + roleDTO.getRoleRid());
	                });
	        newRoles.add(role);
	    }

	    logger.info("New roles prepared for user: {}", userName);

	    // Determine roles to be removed
	    Set<UserRole> rolesToRemove = new HashSet<>();
	    for (UserRole userRole : user.getUserRoles()) {
	        if (!newRoles.contains(userRole.getRole())) {
	            rolesToRemove.add(userRole);
	            logger.info("Role marked for removal: {}", userRole.getRole());
	        }
	    }

	    // Remove roles not present in newRoles
	    logger.info("Removing roles not present in newRoles for user: {}", userName);
	    for (UserRole userRole : rolesToRemove) {
	        user.removeUserRole(userRole); // Remove from user's collection
	        userRole.setUser(null); // Disassociate from user
	        userRoleDao.delete(userRole); // Delete from database
	        logger.info("Removed UserRole: {}", userRole);
	    }

	    // Save changes to the database to ensure roles are removed
	    userRoleDao.flush();
	    userDao.flush(); // Ensure that deletions are flushed to the database

	    // Add new roles
	    logger.info("Adding new roles for user: {}", userName);
	    for (Role role : newRoles) {
	        if (user.getUserRoles().stream().noneMatch(userRole -> userRole.getRole().equals(role))) {
	            user.addRole(role);
	            logger.info("Role added: {}", role);
	        }
	    }

	    // Save updated user
	    userDao.save(user);
	    logger.info("User updated successfully: {}", userName);

	    return new ReqRes(200, null, "User updated successfully");
	}

	public Optional<User> updateUserByUserId(Integer userId, User updatedUser) {
		Optional<User> optionalUser = userDao.findByUserRid(userId);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			// Update other user fields if needed
			user.setUserFirstName(updatedUser.getUserFirstName());
			user.setUserLastName(updatedUser.getUserLastName());
			user.setUserMiddleName(updatedUser.getUserMiddleName());
			user.setUserMobile(updatedUser.getUserMobile());
			user.setUserEmail(updatedUser.getUserEmail());
			user.setUserDesignation(updatedUser.getUserDesignation());

			// Fetch the existing roles for the user
			Set<Role> existingRoles = user.getRoles();

			// Fetch the existing role from the database by name
			Role updatedRole = updatedUser.getRoles().iterator().next(); // Assuming only one role is updated

			// Check if the updated role already exists in the user's roles
			boolean roleExists = existingRoles.stream()
					.anyMatch(role -> role.getRoleName().equals(updatedRole.getRoleName()));

			if (roleExists) {
				// Update the user's roles with the existing ones
				user.setRoles(existingRoles);
				userDao.save(user);
				return Optional.of(user);
			} else {
				// Role does not exist in the user's roles, return empty Optional
				return Optional.empty();
			}
		} else {
			return Optional.empty(); // User not found
		}
	}

	public ReqRes deleteUserByUsernameOld(String username, int userRid) {
		// userDao.deleteUserByUserName(username);
		return new ReqRes(HttpStatus.OK.value(), null, "User deleted successfully");
	}

	public Optional<ReqRes> deleteUserByUserId(Integer userId) {
		Optional<User> optionalUser = userDao.findById(userId);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			// Delete associated roles from user_role table
			user.setRoles(null); // Remove all roles from the user
			userDao.save(user); // Save the user without roles, which will cascade the deletion of associated
								// roles
			userDao.delete(user);
			return Optional.of(new ReqRes(HttpStatus.OK.value(), "", "User deleted successfully"));
		} else {
			return Optional.of(new ReqRes(HttpStatus.NOT_FOUND.value(), "User not found", ""));
		}
	}
	
//	public List<User> getAllUsers() {
//		try {
//			return userDao.findAll();
//		} catch (Exception e) {
//			// Log the exception or handle it as needed
//			return Collections.emptyList(); // Return an empty list in case of an error
//		}
//	}
//
//	public List<User> searchUsersByKeyword(String keyword) {
//		 return userDao.findByUserFirstNameContainingIgnoreCaseOrUserMiddleNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrUserNameContainingIgnoreCase(
//		            keyword, keyword, keyword, keyword);
//	}
	
	// Custom Query Call for better serach performance:

    public List<UserInfo> getAllUsers() {
        try {
            return userDao.findAllUsersWithoutRoles();
        } catch (Exception e) {
            // Log the exception or handle it as needed
            return Collections.emptyList(); // Return an empty list in case of an error
        }
    }

    public List<UserInfo> searchUsersByKeyword(String keyword) {
        try {
            return userDao.searchUsersByKeywordWithoutRoles(keyword);
        } catch (Exception e) {
            // Log the exception or handle it as needed
            return Collections.emptyList(); // Return an empty list in case of an error
        }
    }
	


	public List<User> findByUserName(String userName) {
		Optional<User> userOptional = userDao.findByUserName(userName);
		return userOptional.map(Collections::singletonList).orElseGet(Collections::emptyList);
	}

//	public List<User> findByUserFirstName(String userFirstName) {
//		return userDao.findByUserFirstName(userFirstName);
//	}

//	public User getUserByUsername(String userName) {
//		Optional<User> optionalUser = userDao.findByUserName(userName);
//        return optionalUser.orElse(null);
//	}

//	public User getUserByNames(String name) {
//		Optional<User> optionalUser = userDao
//				.findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrUserMiddleNameContainingIgnoreCase(
//						name, name, name);
//		return optionalUser.orElse(null);
//	}

//	public List<User> findByUserLastName(String userLastName) {
//		// TODO Auto-generated method stub
//		return userDao.findByUserLastName(userLastName);
//	}

	@Transactional
	public void saveUserWithRoles(User user, Set<Role> roles) {
		user.setRoles(roles); // Associate roles with the user
		userDao.save(user); // Persist the user along with associated roles
	}

	private String generateBCryptPassword(String firstName, String lastName) {
		// Generate a random password using the user's first name and last name
		String rawPassword = firstName.substring(0, 1).toLowerCase() + lastName.toLowerCase() + "123";

		// Hash the password using BCrypt
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(rawPassword);
	}

	public User getUserByUsernameForDeletion(String username) {
		return userDao.findByUserName(username).orElse(null);
	}

	public UserDTO getUserByUsername(String username) {
		Optional<User> optionalUser = userDao.findByUserName(username);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			return mapToUserDTO(user);
		}
		return null;
	}

	public UserDTO mapToUserDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setUserFirstName(user.getUserFirstName());
		userDTO.setUserName(user.getUserName());
		userDTO.setUserRid(user.getUserRid());
		userDTO.setUserLastName(user.getUserLastName());
		userDTO.setUserEmail(user.getUserEmail());
		userDTO.setUserEmployeeId(user.getUserEmployeeId());
		userDTO.setRoles(user.getRoles().stream().map(this::mapToRoleDTO).collect(Collectors.toList()));
		return userDTO;
	}

	public RoleDTO mapToRoleDTO(Role role) {
		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setRoleRid(role.getRoleRid());
		roleDTO.setRoleName(role.getRoleName());
		roleDTO.setPermissions(fetchPermissionsForRole(role));
		return roleDTO;
	}

	public List<PermissionDTO> fetchPermissionsForRole(Role role) {
		List<PermissionDTO> permissionDTOs = new ArrayList<>();
		for (RolePermission rolePermission : role.getRolePermissions()) {
			PermissionDTO permissionDTO = mapToPermissionDTO(rolePermission.getPermission());
			permissionDTOs.add(permissionDTO);
		}
		return permissionDTOs;
	}

	private PermissionDTO mapToPermissionDTO(Permission permission) {
		PermissionDTO permissionDTO = new PermissionDTO();
		permissionDTO.setPermissionRid(permission.getPermissionRid());
		permissionDTO.setPermissionName(permission.getPermissionName());
		return permissionDTO;
	}
	
	public UserInfo mapToUserInfo(User user) {
	    return new UserInfo(
	    		user.getUserRid(),
	        user.getUserName(),
	        user.getUserFirstName(),
	        user.getUserLastName(),
	        user.getUserEmail(),
	        user.isActiveUser()
	        
	    );
	}


	// Generate a plain password
	private String generatePlainPassword() {
		// Implement your logic to generate a plain password
		return UUID.randomUUID().toString().replace("-", "").substring(0, 8); // Example: 8-character random string
	}

	// Encrypt the plain password
	private String encryptPassword(String plainPassword) {
		// Implement your encryption logic (e.g., using BCrypt)
		return new BCryptPasswordEncoder().encode(plainPassword);
	}

	
	@Transactional
	public ReqRes deleteUserByUserName(String userName) {
	    try {
	        Optional<User> optionalUser = userDao.findByUserName(userName);
	        if (!optionalUser.isPresent()) {
	            return new ReqRes(404, "Not Found", "User not found");
	        }

	        User user = optionalUser.get();

	        // Collect UserRole mappings for deletion
	        Set<UserRole> userRolesToDelete = new HashSet<>(user.getUserRoles());

	        // Disassociate UserRole mappings
	        user.getUserRoles().forEach(userRole -> userRole.setUser(null));
	        user.getUserRoles().clear();

	        // Delete UserRole mappings
	        userRoleDao.deleteAll(userRolesToDelete);
	        userRoleDao.flush(); // Ensure the deletions are flushed to the database

	        // Delete the user from the database
	        userDao.delete(user);

	        return new ReqRes(200, null, "User deleted successfully");
	    } catch (Exception e) {
	        // Log the error for debugging purposes
	        logger.error("Error occurred while deleting user: {}", userName, e);
	        // Return a response indicating an internal server error
	        return new ReqRes(500, "Internal Server Error", "An error occurred while deleting the user");
	    }
	}

	public ReqRes deactivateUser(String userName) {
        Optional<User> optionalUser = userDao.findByUserName(userName);
        if (!optionalUser.isPresent()) {
            return new ReqRes(404, "Not Found", "User not found");
        }

        User user = optionalUser.get();
        user.setActiveUser(false);
        userDao.save(user);
        return new ReqRes(200, null, "User deactivated successfully");
    }
	
	 @Transactional
	    public User changePassword(String username, String newPassword) {
	        Optional<User> userOptional = userDao.findByUserName(username);
	        User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));
	        user.setUserPassword(passwordEncoder.encode(newPassword));
	        return userDao.save(user);
	    }

}
