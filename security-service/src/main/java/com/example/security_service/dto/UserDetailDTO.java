package com.example.security_service.dto;

import java.util.Set;

public class UserDetailDTO {
    private Long id; // เพิ่ม ID
    private String userName;
    private String password;
    private Set<UserRoleDTO> userRoles;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<UserRoleDTO> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRoleDTO> userRoles) {
        this.userRoles = userRoles;
    }
}
