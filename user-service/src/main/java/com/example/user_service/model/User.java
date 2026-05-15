package com.example.user_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", length = 50, unique = true, nullable = false)
    private String userName;

    // เปลี่ยนจาก @JsonIgnore เป็น @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    // เพื่อให้อ่านค่าจาก JSON ได้ (ตอนสมัคร) แต่ไม่ส่งค่ากลับไปใน JSON (ตอนดึงข้อมูล)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    private boolean suspended;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference("user-roles")
    private Set<UserRoles> userRoles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdDt = LocalDateTime.now();
    }

    // Getter and Setter for userRoles
    public Set<UserRoles> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRoles> userRoles) {
        this.userRoles = userRoles;
    }

    // Convenience methods to add/remove roles
    public void addRole(Roles role) {
        UserRoles userRole = new UserRoles(this, role);
        userRoles.add(userRole);
        role.getUserRoles().add(userRole);
    }

    public void removeRole(Roles role) {
        for (java.util.Iterator<UserRoles> iterator = userRoles.iterator(); iterator.hasNext(); ) {
            UserRoles userRole = iterator.next();
            if (userRole.getUser().equals(this) && userRole.getRole().equals(role)) {
                iterator.remove();
                userRole.getRole().getUserRoles().remove(userRole);
                userRole.setUser(null);
                userRole.setRole(null);
            }
        }
    }


    // Other Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }
}
