package com.example.user_service.dto;

import com.example.user_service.model.User;
import com.example.user_service.model.UserRoles;

import java.util.Set;

/**
 * A DTO specifically for transferring user authentication data to security-service.
 * It includes the password hash, which should not be exposed to the public.
 */
public class UserAuthDTO {
    private Long id;
    private String userName;
    private String password;
    private Set<UserRoles> userRoles;

    public UserAuthDTO(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.userRoles = user.getUserRoles();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<UserRoles> getUserRoles() { return userRoles; }
    public void setUserRoles(Set<UserRoles> userRoles) { this.userRoles = userRoles; }
}
