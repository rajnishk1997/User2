package com.optum.entity;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "rx_user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ur_rid")
    private Integer userRoleRid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "u_rid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "r_rid")
    private Role role;
    
    @Column(name = "ur_created_by")
    private Integer createdBy;

    @Column(name = "ur_modified_by")
    private Integer modifiedBy;

    @Column(name = "ur_create_datetime")
    private Date createdDate;

    @Column(name = "ur_modify_datetime")
    private Date modifiedDate;

    public UserRole() {}

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public Integer getUserRoleRid() {
		return userRoleRid;
	}

	public void setUserRoleRid(Integer userRoleRid) {
		this.userRoleRid = userRoleRid;
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

	public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
