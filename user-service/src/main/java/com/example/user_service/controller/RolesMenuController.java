package com.example.user_service.controller;

import com.example.user_service.model.RolesMenu;
import com.example.user_service.service.RolesMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles-menu")
public class RolesMenuController {

    @Autowired
    private RolesMenuService rolesMenuService;

    @PostMapping("/assign")
    public RolesMenu assignMenuToRole(@RequestParam Long roleId, @RequestParam Long menuId) {
        return rolesMenuService.assignMenuToRole(roleId, menuId);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeMenuFromRole(@RequestParam Long roleId, @RequestParam Long menuId) {
        rolesMenuService.removeMenuFromRole(roleId, menuId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{roleId}")
    public List<RolesMenu> getMenusByRoleId(@PathVariable Long roleId) {
        return rolesMenuService.getMenusByRoleId(roleId);
    }
}
