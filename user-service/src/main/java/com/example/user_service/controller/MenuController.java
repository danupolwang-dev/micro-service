package com.example.user_service.controller;

import com.example.user_service.dto.MenuDTO;
import com.example.user_service.model.Menu;
import com.example.user_service.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Gets menus based on roles provided in a request header.
     * This endpoint is intended to be called by the API Gateway, which injects the user's roles.
     * @param userRoles A comma-separated string of user roles (e.g., "ADMIN,USER").
     * @return A list of menus the user is authorized to see.
     */
    @GetMapping("/my-menus")
    public ResponseEntity<List<MenuDTO>> getMyMenus(@RequestHeader("X-User-Roles") String userRoles) {
        // 1. Convert the comma-separated string from the header into a List of roles.
        List<String> roles = Arrays.asList(userRoles.split(","));

        // 2. Call the service to get menus based on roles.
        List<Menu> menus = menuService.getMenusByRoles(roles);

        // 3. Convert entities to DTOs for the response.
        List<MenuDTO> menuDTOs = menus.stream()
                .map(menu -> new MenuDTO(menu.getId(), menu.getName(), menu.getPath(), menu.getIcon(), menu.getDisplayOrder()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(menuDTOs);
    }

    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }
}
