package com.example.product_service.service;

import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private com.example.product_service.repository.ProductImageRepository productImageRepository;

    @Autowired
    private com.example.product_service.repository.ImageRepository imageRepository;

    public Page<Product> getProducts(String query, Pageable pageable) {
        Page<Product> products = query != null && !query.isEmpty()
            ? productRepository.findByNameContainingOrCodeContaining(query, query, pageable)
            : productRepository.findAll(pageable);
        
        // Enrich with image URLs if needed
        products.forEach(this::enrichProductWithImage);
        return products;
    }

    public Optional<Product> findByProductId(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        productOpt.ifPresent(this::enrichProductWithImage);
        return productOpt;
    }

    private void enrichProductWithImage(Product product) {
        List<com.example.product_service.model.ProductImage> images = productImageRepository.findByProductId(product.getId());
        if (!images.isEmpty()) {
            product.setImageUrl(images.get(0).getImageUrl());
            product.setImageId(images.get(0).getImageId());
        }
    }

    public Product newProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        if (product.getImageId() != null) {
            saveProductImage(savedProduct.getId(), product.getImageId());
        }
        return savedProduct;
    }

    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setCode(productDetails.getCode());
            product.setName(productDetails.getName());
            product.setCategoryCode(productDetails.getCategoryCode());
            product.setPrice(productDetails.getPrice());
            
            Product updatedProduct = productRepository.save(product);
            
            if (productDetails.getImageId() != null) {
                // Remove old links and add new one (assuming 1 image for now)
                productImageRepository.deleteAll(productImageRepository.findByProductId(id));
                saveProductImage(id, productDetails.getImageId());
            }
            
            return updatedProduct;
        }).orElse(null);
    }

    private void saveProductImage(Long productId, Long imageId) {
        imageRepository.findById(imageId).ifPresent(image -> {
            com.example.product_service.model.ProductImage pi = new com.example.product_service.model.ProductImage();
            pi.setProductId(productId);
            pi.setImageId(imageId);
            // Construct view URL
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/products/images/view/")
                .path(image.getFilePath())
                .toUriString();
            pi.setImageUrl(imageUrl);
            pi.setStatus("ACTIVE");
            productImageRepository.save(pi);
        });
    }

    public Product suspendProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setStatus("SUSPENDED");
            return productRepository.save(product);
        }).orElse(null);
    }
    
    public Product activateProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setStatus("ACTIVE");
            return productRepository.save(product);
        }).orElse(null);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Long getProductCount() {
        return productRepository.count();
    }

    public List<Product> getRecentProducts() {
        return productRepository.findAll().stream()
                .sorted((p1, p2) -> p2.getCreatedDt().compareTo(p1.getCreatedDt()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
    }
}
