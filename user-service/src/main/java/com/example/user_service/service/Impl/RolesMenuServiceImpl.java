package com.example.user_service.service.Impl;

import com.example.user_service.model.Menu;
import com.example.user_service.model.Roles;
import com.example.user_service.model.RolesMenu;
import com.example.user_service.repository.MenuRepository;
import com.example.user_service.repository.RolesRepository;
import com.example.user_service.repository.RolesMenuRepository;
import com.example.user_service.service.RolesMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesMenuServiceImpl implements RolesMenuService {

    @Autowired
    private RolesMenuRepository rolesMenuRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public RolesMenu assignMenuToRole(Long roleId, Long menuId) {
        Roles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id " + roleId));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found with id " + menuId));

        RolesMenu rolesMenu = new RolesMenu(role, menu);
        // You might want to set status and other audit fields here
        return rolesMenuRepository.save(rolesMenu);
    }

    @Override
    public void removeMenuFromRole(Long roleId, Long menuId) {
        rolesMenuRepository.deleteByRoleIdAndMenuId(roleId, menuId);
    }

    @Override
    public List<RolesMenu> getMenusByRoleId(Long roleId) {
        return rolesMenuRepository.findByRole_Id(roleId);
    }
}
