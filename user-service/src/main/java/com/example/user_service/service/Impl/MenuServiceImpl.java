package com.example.user_service.service.Impl;

import com.example.user_service.model.Menu;
import com.example.user_service.repository.MenuRepository;
import com.example.user_service.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public Menu saveMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public Optional<Menu> getMenuById(Long id) {
        return menuRepository.findById(id);
    }

    @Override
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    @Override
    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    @Override
    public Menu updateMenu(Long id, Menu menuDetails) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu not found with id " + id));
        menu.setName(menuDetails.getName());
        menu.setPath(menuDetails.getPath());
        menu.setParent(menuDetails.getParent());
        menu.setStatus(menuDetails.getStatus());
        menu.setIcon(menuDetails.getIcon());
        menu.setDisplayOrder(menuDetails.getDisplayOrder());
        return menuRepository.save(menu);
    }

    @Override
    public List<Menu> getMenusByRoles(List<String> roleNames) {
        return menuRepository.findMenusByRoleNames(roleNames);
    }
}
