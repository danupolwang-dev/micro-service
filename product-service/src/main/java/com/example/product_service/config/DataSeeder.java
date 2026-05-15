package com.example.product_service.config;

import com.example.product_service.model.Components;
import com.example.product_service.repository.ComponentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ComponentsRepository componentsRepository;

    @Override
    public void run(String... args) throws Exception {
        // Ensure PRODUCT_CATEGORY group exists
        List<Components> roots = componentsRepository.findByCode("PRODUCT_CATEGORY");
        Components root;
        
        if (roots.isEmpty()) {
            root = new Components();
            root.setName("Product Category");
            root.setCode("PRODUCT_CATEGORY");
            root.setStatus("ACTIVE");
            root = componentsRepository.save(root);
            System.out.println("Seeder: Created PRODUCT_CATEGORY root.");
        } else {
            root = roots.get(0);
        }

        Long parentId = root.getId();

        // Seed Categories if missing
        seedIfMissing("Electronics", "ELECTRONICS", parentId);
        seedIfMissing("Fashion", "FASHION", parentId);
        seedIfMissing("Home & Garden", "HOME_GARDEN", parentId);
        seedIfMissing("Sports", "SPORTS", parentId);
        seedIfMissing("Books", "BOOKS", parentId);
    }

    private void seedIfMissing(String name, String code, Long parentId) {
        List<Components> existing = componentsRepository.findByCode(code);
        if (existing.isEmpty()) {
            Components c = new Components();
            c.setName(name);
            c.setCode(code);
            c.setParent(parentId);
            c.setStatus("ACTIVE");
            componentsRepository.save(c);
            System.out.println("Seeder: Created category " + code);
        }
    }
}
