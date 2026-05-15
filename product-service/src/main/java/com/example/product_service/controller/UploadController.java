package com.example.product_service.controller;

import com.example.product_service.model.Image;
import com.example.product_service.repository.ImageRepository;
import com.example.product_service.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@RestController
@RequestMapping("/api/products/images")
public class UploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ImageRepository imageRepository;

    @Value("${gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        String fileName = fileStorageService.storeFile(file);

        // Save metadata to Database
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setFilePath(fileName); 
        image.setStatus("ACTIVE");
        Image savedImage = imageRepository.save(image);

        // Build the URL to view the image using Gateway URL
        String fileDownloadUri = gatewayUrl + "/api/products/images/view/" + fileName;

        return ResponseEntity.ok(Map.of(
            "id", savedImage.getId(),
            "fileName", fileName,
            "url", fileDownloadUri
        ));
    }
}
