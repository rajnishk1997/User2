package com.optum.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.optum.dao.ReqRes;
import com.optum.dto.ChangePasswordRequest;
import com.optum.dto.UserDTO;
import com.optum.dto.UserInfo;
import com.optum.dto.request.UserRequestDTO;
import com.optum.entity.RegistrationResponse;
import com.optum.entity.ResponseWrapper;
import com.optum.entity.User;

public interface UserControllerInterface {

	void initRoleAndUser();

	ResponseEntity<RegistrationResponse<User>> registerNewUser(UserRequestDTO userRequestDTO);

	ResponseEntity<ResponseWrapper<List<UserInfo>>> getAllUsersCases(String keyword, Boolean isActive);

	ResponseEntity<ReqRes> updateUser(String userName, UserRequestDTO userRequestDTO);

	ResponseEntity<ReqRes> deleteUser(String userName, UserRequestDTO userRequestDTO);

	ResponseEntity<ResponseWrapper<List<UserInfo>>> getNewUsers(int managerId);

	ResponseEntity<ResponseWrapper<ChangePasswordRequest>> acceptNewUser(String userName,
			UserRequestDTO userRequestDTO);

	ResponseEntity<ResponseWrapper<UserDTO>> getUserByUsername(String username);

	ResponseEntity<ReqRes> deactivateGeneralUser(String userName, UserRequestDTO userRequestDTO);

	ResponseEntity<ReqRes> deactivateUser(String userName, UserRequestDTO userRequestDTO);

	ResponseEntity<String> changePassword(ChangePasswordRequest request);

	ResponseEntity<String> firstLoginChangePassword(ChangePasswordRequest request);

	ResponseEntity<ResponseWrapper<List<UserInfo>>> getManagers();

	ResponseEntity<ReqRes> activateUser(String userName, UserRequestDTO userRequestDTO);

	ResponseEntity<List<UserDTO>> getUsersReportingToManager(int managerId, Boolean isActive);

	ResponseEntity<ReqRes> deactivateManager(String userName, UserRequestDTO userRequestDTO);

	String forAdmin();

	String forUser();

}