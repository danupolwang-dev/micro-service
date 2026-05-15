package com.example.user_service.controller;

import com.example.user_service.dto.UpdateStatusRequest;
import com.example.user_service.dto.UserAuthDTO;
import com.example.user_service.model.User;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/details/{username}")
    public ResponseEntity<UserAuthDTO> getUserDetailsForAuth(@PathVariable String username) {
        Optional<UserAuthDTO> userOptional = userService.findUserForAuth(username);
        return userOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUserProfile() {
        Optional<User> userOptional = userService.getUserProfile();
        return userOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        boolean suspend = "suspended".equalsIgnoreCase(request.getStatus());
        User user = userService.updateUserStatus(id, suspend);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return (updatedUser != null) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> getRegisteredUserCount(@RequestParam(defaultValue = "today") String range) {
        return ResponseEntity.ok(userService.getRegisteredUserCount(range));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<User>> getRecentUsers() {
        return ResponseEntity.ok(userService.getRecentUsers());
    }
}
