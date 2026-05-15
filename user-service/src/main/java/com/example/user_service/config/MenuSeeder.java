package com.example.user_service.config;

import com.example.user_service.model.Menu;
import com.example.user_service.model.Roles;
import com.example.user_service.model.RolesMenu;
import com.example.user_service.repository.MenuRepository;
import com.example.user_service.repository.RolesMenuRepository;
import com.example.user_service.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(2) // Run after DataSeeder
public class MenuSeeder implements CommandLineRunner {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RolesMenuRepository rolesMenuRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (menuRepository.count() > 0) {
            return; // Already seeded
        }

        // 1. Create Menus
        Menu dashboard = createMenu("Dashboard", "/dashboard", "fas fa-tachometer-alt", 1);
        Menu users = createMenu("User Management", "/users", "fas fa-users-cog", 2);
        Menu customers = createMenu("Customers", "/customers", "fas fa-user-tie", 3);
        Menu products = createMenu("Products", "/products", "fas fa-boxes", 4);

        // 2. Get Roles
        Roles adminRole = rolesRepository.findByName("ADMIN").orElseThrow();
        Roles userRole = rolesRepository.findByName("USER").orElseThrow();

        // 3. Assign Menus to Roles
        // ADMIN sees everything
        assignMenuToRole(adminRole, dashboard);
        assignMenuToRole(adminRole, users);
        assignMenuToRole(adminRole, customers);
        assignMenuToRole(adminRole, products);

        // USER sees Dashboard, Customers, Products
        assignMenuToRole(userRole, dashboard);
        assignMenuToRole(userRole, customers);
        assignMenuToRole(userRole, products);

        System.out.println("Menu and Role-Menu mappings seeded successfully.");
    }

    private Menu createMenu(String name, String path, String icon, int order) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPath(path);
        menu.setIcon(icon);
        menu.setDisplayOrder(order);
        menu.setStatus("ACTIVE");
        return menuRepository.save(menu);
    }

    private void assignMenuToRole(Roles role, Menu menu) {
        RolesMenu rolesMenu = new RolesMenu(role, menu);
        rolesMenuRepository.save(rolesMenu);
    }
}
