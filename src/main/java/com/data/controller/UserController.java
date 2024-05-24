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
import com.optum.dto.RoleDTO;
import com.optum.dto.UserDTO;
import com.optum.dto.UserInfo;
import com.optum.dto.request.UserRequestDTO;
import com.optum.entity.*;
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
	private RoleDao roleDao;

	@Autowired
	private RolePermissionDao rolePermissionDao;

	@Autowired
	private ModelMapper modelMapper;


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


	@PostMapping({ "/registerNewUser" })
	// @PreAuthorize("hasRole('Admin')")
	public ResponseEntity<RegistrationResponse<User>> registerNewUser(@RequestBody UserRequestDTO userRequestDTO) {
		try {
//			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			if (principal instanceof UserDetails) {
//				String username = ((UserDetails) principal).getUsername();
//				System.out.println("Current logged in user: " + username);
//			} else {
//				System.out.println("No authenticated user found");
//			}
//
//			
			RegistrationResponse<User> registeredUser = userService.registerNewUser(userRequestDTO);

			// Populate the RegistrationResponse object
			RegistrationResponse<User> response = new RegistrationResponse<>(HttpStatus.CREATED.value(), "",
					"User registered successfully", registeredUser.getUserName(), registeredUser.getPassword() 
			);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			// Specific handling for IllegalArgumentException
			RegistrationResponse<User> response = new RegistrationResponse<>(HttpStatus.BAD_REQUEST.value(),
					"Bad Request", e.getMessage(), null, null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} catch (Exception e) {
			// Generic handling for other exceptions
			RegistrationResponse<User> response = new RegistrationResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Internal Server Error", "An error occurred while registering the user", null, null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Method to match useCases:
	
	@GetMapping("/search")
	public ResponseEntity<ResponseWrapper<List<UserInfo>>> getAllUsersCases(@RequestParam(required = true) String keyword) {
	    try {
	        List<User> userList;
	        if (keyword.isEmpty()) {
	            userList = userService.getAllUsers();
	        } else {
	            userList = userService.searchUsersByKeyword(keyword);
	        }

	        List<UserInfo> userInfoList = userList.stream()
	                                              .map(userService::mapToUserInfo)
	                                              .collect(Collectors.toList());

	        ReqRes reqRes;
	        if (userInfoList.isEmpty()) {
	            reqRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "Users not found", "No users found in the database");
	        } else {
	            reqRes = new ReqRes(HttpStatus.OK.value(), null, "Users retrieved successfully");
	        }
	        return ResponseEntity.ok(new ResponseWrapper<>(userInfoList, reqRes));
	    } catch (Exception e) {
	        ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while retrieving users");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>(null, reqRes));
	    }
	}


	
	
	@PutMapping("/update/{userName}")
	public ResponseEntity<ReqRes> updateUser(@PathVariable String userName,
	                                         @RequestBody UserRequestDTO userRequestDTO) {
	    try {
	        ReqRes response = userService.updateUser(userName, userRequestDTO);
	        if (response.getStatusCode() == 200) {
	            return ResponseEntity.ok(response);
	        } else {
	            return ResponseEntity.status(response.getStatusCode()).body(response);
	        }
	    } catch (Exception e) {
	        ReqRes errorResponse = new ReqRes(500, "Internal Server Error", "An error occurred while updating the user");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}
	
	@DeleteMapping("/delete/{userName}")
	public ResponseEntity<ReqRes> deleteUser(@PathVariable String userName) {
	    try {
	        ReqRes response = userService.deleteUserByUserName(userName);
	        if (response.getStatusCode() == 200) {
	            return ResponseEntity.ok(response);
	        } else {
	            return ResponseEntity.status(response.getStatusCode()).body(response);
	        }
	    } catch (Exception e) {
	        ReqRes errorResponse = new ReqRes(500, "Internal Server Error", "An error occurred while deleting the user");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	  
	@GetMapping("/newuser")
	public ResponseEntity<ResponseWrapper<List<UserDTO>>> getNewUsers() {
	    try {
	        List<User> newUsers = userDao.findByIsNewUserTrue();
	        List<UserDTO> userDTOs = newUsers.stream()
	                                          .map(user -> userService.mapToUserDTO(user))
	                                          .collect(Collectors.toList());
	        ResponseWrapper<List<UserDTO>> responseWrapper = new ResponseWrapper<>(userDTOs, new ReqRes(200, null, "Users retrieved successfully"));
	        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	    } catch (Exception e) {
	        ResponseWrapper<List<UserDTO>> errorResponseWrapper = new ResponseWrapper<>(null, new ReqRes(500, "Internal Server Error", "An error occurred while retrieving new users"));
	        return new ResponseEntity<>(errorResponseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


	@PostMapping("/accept/{userName}")
	public ResponseEntity<ReqRes> acceptNewUser(@PathVariable String userName) {
	    try {
	        User user = userDao.findByUserNameAndIsNewUserTrue(userName);
	        if (user != null) {
	            user.setNewUser(false); // Mark the user as not newly created
	            user.setActiveUser(true); // Activate the user
	            userDao.save(user);
	            ReqRes response = new ReqRes(HttpStatus.OK.value(), null, "User accepted successfully");
	            return ResponseEntity.ok(response);
	        } else {
	            ReqRes response = new ReqRes(HttpStatus.NOT_FOUND.value(), "User not found", "No new user found with the provided username");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }
	    } catch (Exception e) {
	        ReqRes response = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while accepting the user");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}


    @GetMapping("get-user-details/{username}")
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
    public ResponseEntity<ReqRes> deactivateUser(@PathVariable String userName) {
        try {
            ReqRes response = userService.deactivateUser(userName);
            if (response.getStatusCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while deactivating the user"));
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