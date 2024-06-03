package com.optum.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rx_audit_trail")
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_rid")
    private Long id;

    @Column(name = "action")
    private String action;

    @Column(name = "status")
    private String status;

    @Column(name = "details")
    private String details;

    @Column(name = "timestamp")
    private Date timestamp;

    @Column(name = "user_id")
    private Integer userId;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

