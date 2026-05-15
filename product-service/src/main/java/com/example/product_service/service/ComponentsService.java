package com.example.product_service.service;

import com.example.product_service.model.Components;
import com.example.product_service.repository.ComponentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComponentsService {

    @Autowired
    private ComponentsRepository componentsRepository;

    public List<Components> getAllComponents() {
        return componentsRepository.findAll();
    }

    public List<Components> getComponentsByParent(Long parentId) {
        return componentsRepository.findByParent(parentId);
    }

    public List<Components> getComponentsByCode(String code) {
        return componentsRepository.findByCode(code);
    }

    public List<Components> getComponentsByParentCode(String parentCode) {
        return componentsRepository.findByParentCode(parentCode);
    }
    
    public Optional<Components> getComponentById(Long id) {
        return componentsRepository.findById(id);
    }

    public Components saveComponent(Components component) {
        return componentsRepository.save(component);
    }

    public void deleteComponent(Long id) {
        componentsRepository.deleteById(id);
    }
}
