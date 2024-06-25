package com.optum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.optum.audit.UserSnapshot;
import com.optum.dao.ReqRes;
import com.optum.dao.UserDao;
import com.optum.dto.ChangePasswordRequest;
import com.optum.dto.UserDTO;
import com.optum.dto.UserInfo;
import com.optum.dto.request.UserRequestDTO;
import com.optum.entity.*;
import com.optum.service.AuditTrailService;
import com.optum.service.RoleService;
import com.optum.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.management.relation.RoleNotFoundException;
@RestController
public class UserController implements UserControllerInterface {
	
	private static final Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	
	@Autowired
	private UserDao userDao;


	@Autowired
    private AuditTrailService auditTrailService;


	@Override
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


	@Override
	@PostMapping("/registerNewUser")
	public ResponseEntity<RegistrationResponse<User>> registerNewUser(@RequestBody UserRequestDTO userRequestDTO) {
	    int currentUserRid = userRequestDTO.getCurrentUserId(); 
	    long startTime = System.currentTimeMillis();
	    try {
	        RegistrationResponse<User> registeredUser = userService.registerNewUser(userRequestDTO);
	        
	        // Log audit trail asynchronously
	        CompletableFuture.runAsync(() -> {
	            String roleNames = RoleService.extractRoleNames(userRequestDTO.getRoles());
	            String details = String.format(
	                "Username: %s, Password: %s, Roles: %s, Email: %s",
	                registeredUser.getUserName(),
	                registeredUser.getPassword(),
	                roleNames,
	                userRequestDTO.getUserEmail()
	            );
	            auditTrailService.logAuditTrailWithUsername("User Created", "SUCCESS", details, currentUserRid);
	        });

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
	        logger.info("Action performed in " + duration + "ms");
	    }
	}


	// Method to match useCases:
	
	@Override
	@GetMapping("/search")
	public ResponseEntity<ResponseWrapper<List<UserInfo>>> getAllUsersCases(@RequestParam(required = false) String keyword,
	                                                                        @RequestParam(required = false) Boolean isActive) {
	    try {
	        List<UserInfo> userList = userService.searchUsersByKeywordAndStatus(keyword, isActive);
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



	
	
	@Override
	@PutMapping("/update/{userName}")
	public ResponseEntity<ReqRes> updateUser(@PathVariable String userName, @RequestBody UserRequestDTO userRequestDTO) {
	    int currentUserRid = userRequestDTO.getCurrentUserId();
	    long startTime = System.currentTimeMillis();
	    try {
	        // Fetch current state of the user
	        Optional<User> optionalUser = userDao.findByUserName(userName);
	        if (!optionalUser.isPresent()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ReqRes(404, "Not Found", "User not found"));
	        }
	        
	        User currentUser = optionalUser.get();
	        // Capture current state of the user for audit logging
	        UserSnapshot currentState = new UserSnapshot(currentUser);
	        
	        // Perform the update
	        ReqRes response = userService.updateUser(userName, userRequestDTO);
	        if (response.getStatusCode() == 200) {
	            // Capture new state for audit logging
	            UserSnapshot newState = new UserSnapshot(userDao.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found after update")));
	            
	            CompletableFuture.runAsync(() -> {
	                try {
	                    String details = userService.generateAuditTrailDetails(currentState, newState, userRequestDTO);
	                    auditTrailService.logAuditTrailWithUsername("User Update", "SUCCESS", details, currentUserRid);
	                } catch (Exception e) {
	                    logger.error("Failed to log audit trail for updateUser action", e);
	                }
	            });
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


	
	 @Override
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

	  
	 @Override
	@GetMapping("/newuser")
	 public ResponseEntity<ResponseWrapper<List<UserInfo>>> getNewUsers(@RequestParam int managerId) {
	     try {
	         List<UserInfo> newUsers = userService.getNewUsers(managerId);
	         ResponseWrapper<List<UserInfo>> responseWrapper = new ResponseWrapper<>(newUsers, new ReqRes(200, null, "Users retrieved successfully"));
	         return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	     } catch (Exception e) {
	         ResponseWrapper<List<UserInfo>> errorResponseWrapper = new ResponseWrapper<>(null, new ReqRes(500, "Internal Server Error", "An error occurred while retrieving new users"));
	         return new ResponseEntity<>(errorResponseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
	     }
	 }



	 @Override
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
	                    changePasswordRequest.setUserName(userName);
	                    changePasswordRequest.setUserPassword(user.getUserPassword());
	                    // changePasswordRequest.setCurrentUserId(currentUserRid);

	                    ResponseWrapper<ChangePasswordRequest> responseWrapper = new ResponseWrapper<>(changePasswordRequest, reqRes);
	                    String details = String.format("Accepted User: %s, Accepted By: %s", userName, currentUserUsername);
	                    auditTrailService.logAuditTrailWithUsername("Accept New User", "SUCCESS", details, currentUserRid);
	                    return ResponseEntity.ok(responseWrapper);
	                } else {
	                    ReqRes notFoundRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "User not found", "User not found after acceptance");
	                    ResponseWrapper<ChangePasswordRequest> responseWrapper = new ResponseWrapper<>(null, notFoundRes);
	                  
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


	 @Override
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
    
    @Override
	@PutMapping("/deactivate-general-user/{userName}")
    public ResponseEntity<ReqRes> deactivateGeneralUser(@PathVariable String userName, @RequestBody UserRequestDTO userRequestDTO) {
        Integer currentUserRid = userRequestDTO.getCurrentUserId(); // Retrieve the current user ID from context/session
        long startTime = System.currentTimeMillis();
        try {
            // Fetch the username of the user performing the action
            String currentUserUsername = userDao.findUserNameByUserRid(currentUserRid);

            ReqRes response = userService.deactivateGeneralUser(userName);
            if (response.getStatusCode() == 200) {
                String details = String.format("Deactivated User: %s, Deactivated By: %s", userName, currentUserUsername);
                auditTrailService.logAuditTrailWithUsername("User Deactivate", "SUCCESS", details, currentUserRid);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while deactivating the user"));
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Action performed in " + duration + "ms");
        }
    }
    
    @Override
	@PutMapping("/deactivate/{userName}")
    public ResponseEntity<ReqRes> deactivateUser(@PathVariable String userName, @RequestBody UserRequestDTO userRequestDTO) {
        Integer adminUserRid = userRequestDTO.getCurrentUserId(); // Retrieve the current admin ID from context/session
        long startTime = System.currentTimeMillis();
        try {
            // Fetch the username of the admin performing the action
            String adminUsername = userDao.findUserNameByUserRid(adminUserRid);

            // Determine if the user is a manager
            boolean isManager = userService.isManager(userName);
            
            ReqRes response;
            if (isManager) {
                response = userService.deactivateManager(userName, adminUserRid);
            } else {
                response = userService.deactivateUser(userName);
            }
            
            if (response.getStatusCode() == 200) {
                String details = String.format("Deactivated User: %s, Deactivated By: %s", userName, adminUsername);
                auditTrailService.logAuditTrailWithUsername("User Deactivate", "SUCCESS", details, adminUserRid);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while deactivating the user"));
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Action performed in " + duration + "ms");
        }
    }


    
    @Override
	@PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Integer currentUserRid = request.getCurrentUserId();
        long startTime = System.currentTimeMillis();
        try {
            userService.changePassword(request.getUserName(), request.getUserPassword());
            String details = "Password changed successfully for username: " + request.getUserName();
            auditTrailService.logAuditTrailWithUsername("changePassword", "SUCCESS", details, currentUserRid);
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Update Password Action performed in " + duration + "ms");
        }
    }
    
    @Override
	@PostMapping("/firstloginpassword")
    public ResponseEntity<String> firstLoginChangePassword(@RequestBody ChangePasswordRequest request) {
        Integer currentUserRid = request.getCurrentUserId();
        long startTime = System.currentTimeMillis();
        try {
            userService.firstLoginChangePassword(request.getUserName(), request.getUserPassword());
            String details = "First Time Password changed successfully for username: " + request.getUserName();
            auditTrailService.logAuditTrailWithUsername("First Time Change Password", "SUCCESS", details, currentUserRid);
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("First Login Change Password Action performed in " + duration + "ms");
        }
    }
    
    @Override
	@GetMapping("/managers")
    public ResponseEntity<ResponseWrapper<List<UserInfo>>> getManagers() {
        try {
            List<UserInfo> userInfos = userService.getManagers();
            ReqRes reqRes = new ReqRes(); 
            ResponseWrapper<List<UserInfo>> response = new ResponseWrapper<>(userInfos, reqRes);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch managers list", ex);
        }
    }
    
    @Override
	@PutMapping("/activate/{userName}")
    public ResponseEntity<ReqRes> activateUser(@PathVariable String userName, @RequestBody UserRequestDTO userRequestDTO) {
        Integer currentUserRid = userRequestDTO.getCurrentUserId(); // Retrieve the current user ID from context/session
        long startTime = System.currentTimeMillis();
        try {
            // Fetch the username of the user performing the action
            String currentUserUsername = userDao.findUserNameByUserRid(currentUserRid);

            ReqRes response = userService.activateUser(userName);
            if (response.getStatusCode() == 200) {
                String details = String.format("Activated User: %s, Activated By: %s", userName, currentUserUsername);
                auditTrailService.logAuditTrailWithUsername("User Activate", "SUCCESS", details, currentUserRid);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while activating the user"));
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Action performed in " + duration + "ms");
        }
    }
    
    @Override
	@GetMapping("/users/reports/{managerId}")
    public ResponseEntity<List<UserDTO>> getUsersReportingToManager(@PathVariable int managerId,
                                                                    @RequestParam(required = false) Boolean isActive) {
        long startTime = System.currentTimeMillis();
        try {
            List<UserDTO> users = userService.getUsersReportingToManager(managerId, isActive);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singletonList(new UserDTO()));
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Action performed in " + duration + "ms");
        }
    }


    @Override
	@PutMapping("/deactivateManager/{userName}")
    public ResponseEntity<ReqRes> deactivateManager(@PathVariable String userName, @RequestBody UserRequestDTO userRequestDTO) {
        Integer adminUserRid = userRequestDTO.getCurrentUserId(); // Retrieve the current admin ID from context/session
        long startTime = System.currentTimeMillis();
        try {
            // Fetch the username of the admin performing the action
            String adminUsername = userDao.findUserNameByUserRid(adminUserRid);

            ReqRes response = userService.deactivateManager(userName, adminUserRid);
            if (response.getStatusCode() == 200) {
                String details = String.format("Deactivated Manager: %s, Deactivated By: %s", userName, adminUsername);
                auditTrailService.logAuditTrailWithUsername("Manager Deactivate", "SUCCESS", details, adminUserRid);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while deactivating the manager"));
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Action performed in " + duration + "ms");
        }
    }




	@Override
	@GetMapping({ "/forAdmin" })
	@PreAuthorize("hasRole('Admin')")
	public String forAdmin() {
		return "This URL is only accessible to the admin";
	}

	@Override
	@GetMapping({ "/forUser" })
	@PreAuthorize("hasRole('User')")
	public String forUser() {
		return "This URL is only accessible to the user";
	}
	


}