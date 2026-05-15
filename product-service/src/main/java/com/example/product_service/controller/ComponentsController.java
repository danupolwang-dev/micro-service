package com.example.product_service.controller;

import com.example.product_service.model.Components;
import com.example.product_service.service.ComponentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/components")
public class ComponentsController {

    @Autowired
    private ComponentsService componentsService;

    @GetMapping
    public List<Components> getAllComponents() {
        return componentsService.getAllComponents();
    }

    @GetMapping("/parent/{parentId}")
    public List<Components> getByParent(@PathVariable Long parentId) {
        return componentsService.getComponentsByParent(parentId);
    }

    @GetMapping("/code/{code}")
    public List<Components> getByCode(@PathVariable String code) {
        return componentsService.getComponentsByCode(code);
    }

    @GetMapping("/list/{parentCode}")
    public List<Components> getByParentCode(@PathVariable String parentCode) {
        return componentsService.getComponentsByParentCode(parentCode);
    }

    @PostMapping
    public ResponseEntity<Components> createComponent(@RequestBody Components component) {
        return ResponseEntity.ok(componentsService.saveComponent(component));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComponent(@PathVariable Long id) {
        componentsService.deleteComponent(id);
        return ResponseEntity.noContent().build();
    }
}
