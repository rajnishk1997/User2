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

//    @PostConstruct   
//    public void initPermissions() {
//    	userService.initPermissions();
//    }

	@PostConstruct // PostConstruct as I wish to run this code once the compilation is done.
	public void initRoleAndUser() {
		userService.initRoleAndUser();
	}

	@PostMapping({ "/registerNewUser" })
	// @PreAuthorize("hasRole('Admin')")
	public ResponseEntity<RegistrationResponse<User>> registerNewUser(@RequestBody UserRequestDTO userRequestDTO) {
		try {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof UserDetails) {
				String username = ((UserDetails) principal).getUsername();
				System.out.println("Current logged in user: " + username);
			} else {
				System.out.println("No authenticated user found");
			}

			
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
	
	@GetMapping("/admin/get-all-users-case")
	public ResponseEntity<ResponseWrapper<List<UserDTO>>> getAllUsersCases(@RequestParam(required = true) String keyword) {
	    try {
	        List<User> userList;
	        if (keyword.isEmpty()) {
	            userList = userService.getAllUsers();
	        } else {
	            userList = userService.searchUsersByKeyword(keyword);
	        }

	        List<UserDTO> userDTOList = userList.stream()
	                                             .map(userService::mapToUserDTO)
	                                             .collect(Collectors.toList());

	        ReqRes reqRes;
	        if (userDTOList.isEmpty()) {
	            reqRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "Users not found", "No users found in the database");
	        } else {
	            reqRes = new ReqRes(HttpStatus.OK.value(), null, "Users retrieved successfully");
	        }
	        return ResponseEntity.ok(new ResponseWrapper<>(userDTOList, reqRes));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	
	
	@PutMapping("/update/{userName}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable String userName,
                                             @RequestBody UserRequestDTO userRequestDTO) {
        ReqRes response = userService.updateUser(userName, userRequestDTO);
        if (response.getStatusCode() == 200) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
    }
	
	
//	@DeleteMapping("/admin/delete/{userName}")
//	//@PreAuthorize("hasRole('Admin')")
//	public ResponseEntity<ResponseWrapper<ReqRes>> deleteUser(@PathVariable String userName) {
//		try {
//			java.util.Optional<ReqRes> optionalReqRes = userService.deleteUserByUsername(userName);
//			if (optionalReqRes.isPresent()) {
//				ReqRes reqRes = optionalReqRes.get();
//				return ResponseEntity.ok(new ResponseWrapper<>(reqRes, reqRes));
//			} else {
//				ReqRes reqRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "User not found", "");
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(reqRes, reqRes));
//			}
//		} catch (Exception e) {
//			ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
//					"An error occurred while deleting the user");
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>(reqRes, reqRes));
//		}
//	}
	
	  @DeleteMapping("/delete/{username}")
	    public ResponseEntity<ReqRes> deleteUser(@PathVariable String username) {
	        try {
	        	 User user = userService.getUserByUsernameForDeletion(username);
	        	    
	        	    if (user != null) {
	        	        int userRid = user.getUserRid(); // Obtain the u_rid from the fetched User entity
	        	        
	        	        // Call the deleteUserByUsername method with the obtained u_rid
	        	        ReqRes isDeleted = userService.deleteUserByUsername(username, userRid);
	        	        
	        	        return ResponseEntity.ok(isDeleted);
	        	    } else {
	        	        // Handle the case where the user with the given username is not found
	        	        return ResponseEntity.notFound().build();
	        	    }
	            
	           
	        } catch (Exception e) {
	            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting user", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ReqRes());
	        }
	    }
	  
	  @GetMapping("/newuser")
	  public ResponseEntity<ResponseWrapper<List<UserDTO>>> getNewUsers() {
	      List<User> newUsers = userDao.findByIsNewUserTrue();
	      List<UserDTO> userDTOs = newUsers.stream().map(user -> userService.mapToUserDTO(user)).collect(Collectors.toList());
	      ResponseWrapper<List<UserDTO>> responseWrapper = new ResponseWrapper<List<UserDTO>>(userDTOs, new ReqRes("success", "Users retrieved successfully"));
	      return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	  }

	  @PostMapping("/accept/{userName}")
	    public ResponseEntity<ReqRes> acceptNewUser(@PathVariable String userName) {
	        User user = userDao.findByUserNameAndIsNewUserTrue(userName);
	        if (user != null) {
	            user.setNewUser(false); // Mark the user as not newly created
	            userDao.save(user);
	            return new ResponseEntity<>(new ReqRes(HttpStatus.OK.value(), null, "User accepted successfully"), HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(new ReqRes(HttpStatus.NOT_FOUND.value(), "User not found", "No new user found with the provided username"), HttpStatus.NOT_FOUND);
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
	
	// Method to match exact user
    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ResponseWrapper<List<UserDTO>>> getAllUsers(@RequestParam(required = false) String userName,
            @RequestParam(required = false) String userFirstName) {
        try {
            List<User> userList;
            if (userName != null) {
                userList = userService.findByUserName(userName);
            } else if (userFirstName != null) {
                userList = userService.findByUserFirstName(userFirstName);
            } else {
                userList = userService.getAllUsers();
            }

            List<UserDTO> userDTOList = userList.stream()
                    .map(userService::mapToUserDTO)
                    .collect(Collectors.toList());

            ReqRes reqRes;
            if (userDTOList.isEmpty()) {
                reqRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "Users not found", "No users found in the database");
            } else {
                reqRes = new ReqRes(HttpStatus.OK.value(), null, "Users retrieved successfully");
            }
            return ResponseEntity.ok(new ResponseWrapper<>(userDTOList, reqRes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}