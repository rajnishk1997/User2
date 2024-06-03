package com.optum.audit;

import java.util.Set;
import java.util.stream.Collectors;

import com.optum.entity.User;

public class UserSnapshot {
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;

    public UserSnapshot(User user) {
        this.firstName = user.getUserFirstName();
        this.lastName = user.getUserLastName();
        this.email = user.getUserEmail();
        this.roles = user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getRoleName())
                        .collect(Collectors.toSet());
    }

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

    
}