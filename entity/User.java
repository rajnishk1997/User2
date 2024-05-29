package com.optum.entity;

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rx_user", indexes = {
        @Index(name = "idx_u_username", columnList = "u_username"),
        @Index(name = "idx_u_first_name", columnList = "u_first_name"),
        @Index(name = "idx_u_last_name", columnList = "u_last_name"),
        @Index(name = "idx_u_user_email", columnList = "u_email")
})

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_rid")
    private int userRid;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();
    
    @Column(name = "u_username")
    private String userName;
    @Column(name = "u_first_name")
    private String userFirstName;
    @Column(name = "u_middle_name")
    private String userMiddleName;
    @Column(name = "u_last_name")
    private String userLastName;
    @Column(name = "u_full_name")
    private String userFullName;
    @Column(name = "u_password")
    private String userPassword;
    @Column(name = "u_mobile")
    private String userMobile;
    @Column(name = "u_email")
    private String userEmail;
    @Column(name = "u_designation")
    private String UserDesignation;
    @Column(name = "u_employee_id")
    private String userEmployeeId;
    @Column(name = "u_roleName")
    private String roleNames;
    @Column(name = "u_isactive")
    private boolean isActiveUser = false;
    @Column(name = "u_plain_password")
    private String userPlainPassword; // New field for plain password
    private Integer currentUserId;
    private boolean isNewUser=true;
    
    public void addRole(Role role) {
        UserRole userRole = new UserRole(this, role);
        userRoles.add(userRole);
    }

    public void removeRole(Role role) {
        userRoles.removeIf(userRole -> userRole.getRole().equals(role));
    }
    
    public Set<Role> getRoles() {
        Set<Role> roles = new HashSet<>();
        for (UserRole userRole : this.userRoles) {
            roles.add(userRole.getRole());
        }
        return roles;
    }
    
    public void setRoles(Set<Role> roles) {
        if (this.userRoles == null) {
            this.userRoles = new HashSet<>();
        } else {
            this.userRoles.clear();
        }

        for (Role role : roles) {
            UserRole userRole = new UserRole(this, role);
            this.userRoles.add(userRole);
        }
    }
    
 // Method to add user role
    public void addUserRole(UserRole userRole) {
        userRoles.add(userRole);
        userRole.setUser(this);
    }

    // Method to remove user role
    public void removeUserRole(UserRole userRole) {
        userRoles.remove(userRole);
        userRole.setUser(null);
    }
    
    public String getUserPlainPassword() {
		return userPlainPassword;
	}

	public void setUserPlainPassword(String userPlainPassword) {
		this.userPlainPassword = userPlainPassword;
	}

	@Column(name = "u_created_by")
    private Integer createdBy;

    @Column(name = "u_modified_by")
    private Integer modifiedBy;

    @Column(name = "u_create_datetime")
    private Date createdDate;

    @Column(name = "u_modify_datetime")
    private Date modifiedDate;
	

	public Integer getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Set<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	

	public boolean isNewUser() {
		return isNewUser;
	}

	public void setNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}

	public int getUserRid() {
		return userRid;
	}

	public void setUserRid(int userRid) {
		this.userRid = userRid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserMiddleName() {
		return userMiddleName;
	}

	public void setUserMiddleName(String userMiddleName) {
		this.userMiddleName = userMiddleName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserDesignation() {
		return UserDesignation;
	}

	public void setUserDesignation(String userDesignation) {
		UserDesignation = userDesignation;
	}

	public String getUserEmployeeId() {
		return userEmployeeId;
	}

	public void setUserEmployeeId(String userEmployeeId) {
		this.userEmployeeId = userEmployeeId;
	}

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public boolean isActiveUser() {
		return isActiveUser;
	}

	public void setActiveUser(boolean isActiveUser) {
		this.isActiveUser = isActiveUser;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
	
	

    // Getters and setters
    
    
}