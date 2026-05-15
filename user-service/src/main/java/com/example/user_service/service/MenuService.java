package com.example.user_service.service;

import com.example.user_service.model.Menu;
import java.util.List;
import java.util.Optional;

public interface MenuService {
    Menu saveMenu(Menu menu);

    Optional<Menu> getMenuById(Long id);

    List<Menu> getAllMenus();

    void deleteMenu(Long id);

    Menu updateMenu(Long id, Menu menuDetails);

    // RBAC: Get menus by user roles
    List<Menu> getMenusByRoles(List<String> roleNames);
}
