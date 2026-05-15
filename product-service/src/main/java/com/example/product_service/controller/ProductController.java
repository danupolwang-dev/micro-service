package com.example.product_service.controller;

import com.example.product_service.model.Product;
import com.example.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProducts(query, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findByProductId(@PathVariable Long id) {
        return productService.findByProductId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product newProduct(@RequestBody Product product) {
        return productService.newProduct(product);
    } 

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return (updatedProduct != null) ? ResponseEntity.ok(updatedProduct) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<Product> suspendProduct(@PathVariable Long id) {
        Product suspendedProduct = productService.suspendProduct(id);
        return (suspendedProduct != null) ? ResponseEntity.ok(suspendedProduct) : ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<Product> activateProduct(@PathVariable Long id) {
        Product activatedProduct = productService.activateProduct(id);
        return (activatedProduct != null) ? ResponseEntity.ok(activatedProduct) : ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> getProductCount() {
        return ResponseEntity.ok(productService.getProductCount());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Product>> getRecentProducts() {
        return ResponseEntity.ok(productService.getRecentProducts());
    }
}
