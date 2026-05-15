package com.example.user_service.controller;

import com.example.user_service.model.UserRoles;
import com.example.user_service.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
public class UserRolesController {

    @Autowired
    private UserRolesService userRolesService;

    @PostMapping("/assign")
    public UserRoles assignRoleToUser(@RequestParam Long userId, @RequestParam Long roleId) {
        return userRolesService.assignRoleToUser(userId, roleId);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeRoleFromUser(@RequestParam Long userId, @RequestParam Long roleId) {
        userRolesService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public List<UserRoles> getRolesByUserId(@PathVariable Long userId) {
        return userRolesService.getRolesByUserId(userId);
    }
}
