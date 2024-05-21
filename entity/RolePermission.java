package com.optum.entity;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "rx_role_permission")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rp_rid")
    private int rolePermissionRid;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;
    
    @Column(name = "rp_created_by")
    private Integer createdBy;

    @Column(name = "rp_modified_by")
    private Integer modifiedBy;

    @Column(name = "rp_create_datetime")
    private Date createdDate;

    @Column(name = "rp_modify_datetime")
    private Date modifiedDate;

    public RolePermission() {}

    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
    }

    

    public int getRolePermissionRid() {
		return rolePermissionRid;
	}

	public void setRolePermissionRid(int rolePermissionRid) {
		this.rolePermissionRid = rolePermissionRid;
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

	public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
