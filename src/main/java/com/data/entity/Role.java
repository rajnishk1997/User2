package com.optum.entity;

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rx_role")
public class Role {
    

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name = "r_rid")
    private int roleRid;
    

	@Column(name = "r_role_name")
    private String roleName;
    
    @Column(name = "r_role_description")
    private String roleDescription;
    
    @Column(name = "r_created_by")
    private Integer createdBy;

    @Column(name = "r_modified_by")
    private Integer modifiedBy;

    @Column(name = "r_create_datetime")
    private Date createdDate;

    @Column(name = "r_modify_datetime")
    private Date modifiedDate;
    
    public Role(String string, String name) {
		// TODO Auto-generated constructor stub
    	this.roleName= name;
	}

    
    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermission> rolePermissions = new HashSet<>();
    
    

    public void addRolePermission(RolePermission rolePermission) {
        this.rolePermissions.add(rolePermission);
        rolePermission.setRole(this);
    }

    public void removeRolePermission(RolePermission rolePermission) {
        this.rolePermissions.remove(rolePermission);
        rolePermission.setRole(null);
    }

    public void setPermissions(Set<Permission> permissions) {
        if (this.rolePermissions == null) {
            this.rolePermissions = new HashSet<>();
        } else {
            this.rolePermissions.clear();
        }

        for (Permission permission : permissions) {
            RolePermission rolePermission = new RolePermission(this, permission);
            this.rolePermissions.add(rolePermission);
        }
    }


    public Role() {}

    public Role(String name) {
        this.roleName = name;
    }
   

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
    


    public Set<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }
    
    
   

	public int getRoleRid() {
		return roleRid;
	}

	public void setRoleRid(int roleRid) {
		this.roleRid = roleRid;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
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