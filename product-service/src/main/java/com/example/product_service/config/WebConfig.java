package com.example.product_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(uploadDir);
        String absolutePath = path.toFile().getAbsolutePath();
        
        // Expose the upload directory via /api/products/images/view/**
        // This allows frontend to access images via URL
        registry.addResourceHandler("/api/products/images/view/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
