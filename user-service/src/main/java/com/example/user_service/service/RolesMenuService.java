package com.example.user_service.service;

import com.example.user_service.model.RolesMenu;
import java.util.List;

public interface RolesMenuService {
    RolesMenu assignMenuToRole(Long roleId, Long menuId);

    void removeMenuFromRole(Long roleId, Long menuId);

    List<RolesMenu> getMenusByRoleId(Long roleId);
}
