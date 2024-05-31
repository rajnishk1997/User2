package com.optum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;

import com.optum.dao.ReqRes;
import com.optum.dao.RoleDao;
import com.optum.dao.RolePermissionDao;
import com.optum.dao.UserDao;
import com.optum.dao.UserRoleDao;
import com.optum.dto.ChangePasswordRequest;
import com.optum.dto.RoleDTO;
import com.optum.dto.UserDTO;
import com.optum.dto.UserInfo;
import com.optum.dto.request.UserRequestDTO;
import com.optum.entity.*;
import com.optum.service.AuditTrailService;
import com.optum.service.UserService;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

@RestController
public class UserController {
	
	private static final Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDao userDao;


	@Autowired
    private AuditTrailService auditTrailService;


	@PostConstruct // PostConstruct as I wish to run this code once the compilation is done.
	public void initRoleAndUser() {
	    try {
	        userService.initRoleAndUser();
	        System.out.println("Roles and users initialized successfully.");
	    } catch (Exception e) {
	        // Log the exception and print a meaningful error message
	        e.printStackTrace();
	        System.err.println("An error occurred while initializing roles and users: " + e.getMessage());
	    }
	}


	@PostMapping("/registerNewUser")
	public ResponseEntity<RegistrationResponse<User>> registerNewUser(@RequestBody UserRequestDTO userRequestDTO) {
	    int currentUserRid = userRequestDTO.getCurrentUserId(); 
	    long startTime = System.currentTimeMillis();
	    try {
	        RegistrationResponse<User> registeredUser = userService.registerNewUser(userRequestDTO);
	        
	        // Prepare audit trail details
	        String details = String.format("Username: %s, Password: %s, Roles: %s, Email: %s",
	                registeredUser.getUserName(),
	                registeredUser.getPassword(),
	                userRequestDTO.getRoles().toString(),
	                userRequestDTO.getUserEmail());

	        // Log audit trail asynchronously
	        auditTrailService.logAuditTrailWithUsername("User Created", "SUCCESS", details, currentUserRid);

	        // Create and return the registration response
	        RegistrationResponse<User> response = new RegistrationResponse<>(HttpStatus.CREATED.value(), "",
	                "User registered successfully", registeredUser.getUserName(), registeredUser.getPassword());

	        return ResponseEntity.status(HttpStatus.CREATED).body(response);
	    } catch (IllegalArgumentException e) {
	        RegistrationResponse<User> response = new RegistrationResponse<>(HttpStatus.BAD_REQUEST.value(),
	                "Bad Request", e.getMessage(), null, null);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    } catch (Exception e) {
	        RegistrationResponse<User> response = new RegistrationResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                "Internal Server Error", "An error occurred while registering the user", null, null);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    } finally {
	        long endTime = System.currentTimeMillis();
	        long duration = endTime - startTime;
	        logger.info("User Creation Action performed in " + duration + "ms");
	    }
	}

	// Method to match useCases:
	
	@GetMapping("/search")
	public ResponseEntity<ResponseWrapper<List<UserInfo>>> getAllUsersCases(@RequestParam(required = true) String keyword) {
	    try {
	        List<UserInfo> userList;
	        if (keyword.isEmpty()) {
	            userList = userService.getAllUsers();
	        } else {
	            userList = userService.searchUsersByKeyword(keyword);
	        }
	        ReqRes reqRes;
	        if (userList.isEmpty()) {
	            reqRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "Users not found", "No users found in the database");
	        } else {
	            reqRes = new ReqRes(HttpStatus.OK.value(), null, "Users retrieved successfully");
	        }
	        return ResponseEntity.ok(new ResponseWrapper<>(userList, reqRes));
	    } catch (Exception e) {
	        ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while retrieving users");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>(null, reqRes));
	    }
	}


	
	
	@PutMapping("/update/{userName}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable String userName,
                                             @RequestBody UserRequestDTO userRequestDTO) {
		int currentUserRid = userRequestDTO.getCurrentUserId(); 
        long startTime = System.currentTimeMillis();
        try {
            ReqRes response = userService.updateUser(userName, userRequestDTO);
            if (response.getStatusCode() == 200) {
                auditTrailService.logAuditTrailWithUsername("updateUser", "SUCCESS", "User updated successfully", currentUserRid);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            ReqRes errorResponse = new ReqRes(500, "Internal Server Error", "An error occurred while updating the user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Action performed in " + duration + "ms");
        }
    }
	
	 @DeleteMapping("/delete/{userName}")
	    public ResponseEntity<ReqRes> deleteUser(@PathVariable String userName, @RequestBody UserRequestDTO userRequestDTO) {
	        int currentUserRid = userRequestDTO.getCurrentUserId(); 
	        long startTime = System.currentTimeMillis();
	        try {
	            // Fetch the username of the user performing the deletion
	            String currentUserUsername = userDao.findUserNameByUserRid(currentUserRid);

	            ReqRes response = userService.deleteUserByUserName(userName);
	            if (response.getStatusCode() == 200) {
	                String details = String.format("Deleted User: %s, Deleted By: %s", userName, currentUserUsername);
	                auditTrailService.logAuditTrailWithUsername("User Deletion", "SUCCESS", details, currentUserRid);
	                return ResponseEntity.ok(response);
	            } else {
	                return ResponseEntity.status(response.getStatusCode()).body(response);
	            }
	        } catch (Exception e) {
	            ReqRes errorResponse = new ReqRes(500, "Internal Server Error", "An error occurred while deleting the user");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	        } finally {
	            long endTime = System.currentTimeMillis();
	            long duration = endTime - startTime;
	            logger.info("Deletion Action performed in " + duration + "ms");
	        }
	    }

	  
	@GetMapping("/newuser")
    public ResponseEntity<ResponseWrapper<List<UserInfo>>> getNewUsers() {
        try {
            List<UserInfo> newUsers = userService.getNewUsers();
            ResponseWrapper<List<UserInfo>> responseWrapper = new ResponseWrapper<>(newUsers, new ReqRes(200, null, "Users retrieved successfully"));
            return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
        } catch (Exception e) {
            ResponseWrapper<List<UserInfo>> errorResponseWrapper = new ResponseWrapper<>(null, new ReqRes(500, "Internal Server Error", "An error occurred while retrieving new users"));
            return new ResponseEntity<>(errorResponseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	 @PostMapping("/accept/{userName}")
	    public ResponseEntity<ResponseWrapper<ChangePasswordRequest>> acceptNewUser(@PathVariable String userName, @RequestBody UserRequestDTO userRequestDTO) {
	        Integer currentUserRid = userRequestDTO.getCurrentUserId();
	        long startTime = System.currentTimeMillis();
	        try {
	            // Fetch the username of the user performing the action
	            String currentUserUsername = userDao.findUserNameByUserRid(currentUserRid);

	            ReqRes reqRes = userService.acceptNewUser(userName, userRequestDTO);
	            if (reqRes.getStatusCode() == HttpStatus.OK.value()) {
	                Optional<User> optionalUser = userDao.findByUserName(userName);
	                if (optionalUser.isPresent()) {
	                    User user = optionalUser.get();
	                    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
	                    changePasswordRequest.setUsername(userName);
	                    changePasswordRequest.setNewPassword(user.getUserPassword());
	                    // changePasswordRequest.setCurrentUserId(currentUserRid);

	                    ResponseWrapper<ChangePasswordRequest> responseWrapper = new ResponseWrapper<>(changePasswordRequest, reqRes);
	                    String details = String.format("Accepted User: %s, Accepted By: %s", userName, currentUserUsername);
	                    auditTrailService.logAuditTrailWithUsername("Accept New User", "SUCCESS", details, currentUserRid);
	                    return ResponseEntity.ok(responseWrapper);
	                } else {
	                    ReqRes notFoundRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "User not found", "User not found after acceptance");
	                    ResponseWrapper<ChangePasswordRequest> responseWrapper = new ResponseWrapper<>(null, notFoundRes);
	                    String details = String.format("Failed to find User: %s after acceptance, Attempted By: %s", userName, currentUserUsername);
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
	                }
	            } else {
	                ResponseWrapper<ChangePasswordRequest> responseWrapper = new ResponseWrapper<>(null, reqRes);
	                return ResponseEntity.status(reqRes.getStatusCode()).body(responseWrapper);
	            }
	        } catch (Exception e) {
	            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while accepting the user");
	            ResponseWrapper<ChangePasswordRequest> responseWrapper = new ResponseWrapper<>(null, reqRes);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
	        } finally {
	            long endTime = System.currentTimeMillis();
	            long duration = endTime - startTime;
	            logger.info("Accept Action performed in " + duration + "ms");
	        }
	    }


    @GetMapping("/get-user-details/{username}")
    public ResponseEntity<ResponseWrapper<UserDTO>> getUserByUsername(@PathVariable String username) {
        try {
            UserDTO user = userService.getUserByUsername(username);
            if (user != null) {
                ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), null, "User retrieved successfully");
                return ResponseEntity.ok(new ResponseWrapper<>(user, reqRes));
            } else {
                ReqRes reqRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "User not found", "User with the given username not found");
                return ResponseEntity.ok(new ResponseWrapper<>(null, reqRes));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PutMapping("/deactivate/{userName}")
    public ResponseEntity<ReqRes> deactivateUser(@PathVariable String userName, @RequestBody UserRequestDTO userRequestDTO) {
        Integer currentUserRid = userRequestDTO.getCurrentUserId(); // Retrieve the current user ID from context/session
        long startTime = System.currentTimeMillis();
        try {
            // Fetch the username of the user performing the action
            String currentUserUsername = userDao.findUserNameByUserRid(currentUserRid);

            ReqRes response = userService.deactivateUser(userName);
            if (response.getStatusCode() == 200) {
                String details = String.format("Deactivated User: %s, Deactivated By: %s", userName, currentUserUsername);
                auditTrailService.logAuditTrailWithUsername("User Deactivate", "SUCCESS", details, currentUserRid);
                return ResponseEntity.ok(response);
            } else {
                String details = String.format("Failed to deactivate User: %s, Attempted By: %s. Reason: %s", userName, currentUserUsername, response.getMessage());
             //   auditTrailService.logAuditTrail("User Deactivate", "FAILURE", details, currentUserRid, new Date());
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            String currentUserUsername = userDao.findUserNameByUserRid(currentUserRid);
            String details = String.format("Error occurred while deactivating User: %s, Attempted By: %s", userName, currentUserUsername);
       //     auditTrailService.logAuditTrail("User Deactivate", "FAILURE", details, currentUserRid, new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while deactivating the user"));
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Action performed in " + duration + "ms");
        }
    }

    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request, @RequestBody UserRequestDTO userRequestDTO) {
        Integer currentUserRid = userRequestDTO.getCurrentUserId(); // Retrieve the current user ID from context/session
        long startTime = System.currentTimeMillis();
        try {
            userService.changePassword(request.getUsername(), request.getNewPassword());
            String details = "Password changed successfully for username: " + request.getUsername();
            auditTrailService.logAuditTrailWithUsername("changePassword", "SUCCESS", details, currentUserRid);
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            String details = "Failed to change password for username: " + request.getUsername() + ". Error: " + e.getMessage();
          //  auditTrailService.logAuditTrailWithUsername("changePassword", "FAILURE", details, currentUserRid);
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Action performed in " + duration + "ms");
        }
    }


	@GetMapping({ "/forAdmin" })
	@PreAuthorize("hasRole('Admin')")
	public String forAdmin() {
		return "This URL is only accessible to the admin";
	}

	@GetMapping({ "/forUser" })
	@PreAuthorize("hasRole('User')")
	public String forUser() {
		return "This URL is only accessible to the user";
	}
	


}