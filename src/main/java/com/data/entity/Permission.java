package com.optum.entity;

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rx_permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_rid")
    private int permissionRid;

    @Column(name = "p_permission_name")
    private String permissionName;
    
    @Column(name = "p_created_by")
    private Integer createdBy;

    @Column(name = "p_modified_by")
    private Integer modifiedBy;

    @Column(name = "p_create_datetime")
    private Date createdDate;

    @Column(name = "p_modify_datetime")
    private Date modifiedDate;

    @OneToMany(mappedBy = "permission", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermission> rolePermissions = new HashSet<>();

    public Permission(String string) {
    	
    	this.permissionName=string;
    }

    

    public Permission() {
		// TODO Auto-generated constructor stub
	}



	public int getPermissionRid() {
		return permissionRid;
	}



	public void setPermissionRid(int permissionRid) {
		this.permissionRid = permissionRid;
	}



	public String getPermissionName() {
		return permissionName;
	}



	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
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



	public Set<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }
}
