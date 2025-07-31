package com.golden.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageService {
    
    // Standard dimensions for gallery images
    private static final int STANDARD_WIDTH = 800;
    private static final int STANDARD_HEIGHT = 600;
    private static final String ALLOWED_TYPES = "image/jpeg,image/png,image/jpg";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    public void deleteImage(String imagePath) throws IOException {
        if (imagePath != null && !imagePath.isEmpty()) {
            Path path = Paths.get(uploadDir, imagePath);
            Files.deleteIfExists(path);
            }
        }
    

    public String saveImage(MultipartFile file) throws IOException {
        validateImage(file);
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            System.out.println("Upload directory (before creation): " + uploadPath);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created directory: " + uploadPath);
            } else {
                System.out.println("Directory already exists: " + uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save the original file directly
            Path filePath = uploadPath.resolve(newFilename);
            System.out.println("Attempting to save file to: " + filePath);
            System.out.println("File exists before save: " + Files.exists(filePath));
            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("File exists after save: " + Files.exists(filePath));
            System.out.println("File size: " + Files.size(filePath));
            
            // Return the URL path for the image
            return "images/products/" + newFilename;
        } catch (Exception e) {
            System.err.println("Error saving image: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String saveBase64Image(String base64Image) throws IOException {
        // Remove data:image/jpeg;base64, if present
        String imageData = base64Image;
        String fileExtension = ".jpg"; // Default to jpg
        
        if (base64Image.contains(",")) {
            String[] parts = base64Image.split(",");
            imageData = parts[1];
            if (parts[0].contains("image/png")) {
                fileExtension = ".png";
            }
        }
        
        // Decode base64 to bytes
        byte[] imageBytes = Base64.getDecoder().decode(imageData);
        
        // Convert to BufferedImage
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        
        // Resize image
        BufferedImage standardizedImage = standardizeImage(originalImage);
        
        // Convert back to base64
        return encodeToBase64(standardizedImage);
    }

    private BufferedImage standardizeImage(BufferedImage original) {
        // Calculate dimensions while maintaining aspect ratio
        double aspectRatio = (double) original.getWidth() / original.getHeight();
        int targetWidth = STANDARD_WIDTH;
        int targetHeight = STANDARD_HEIGHT;

        if (aspectRatio > (double) STANDARD_WIDTH / STANDARD_HEIGHT) {
            targetHeight = (int) (STANDARD_WIDTH / aspectRatio);
        } else {
            targetWidth = (int) (STANDARD_HEIGHT * aspectRatio);
        }

        // Create new image with standard size
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        return resized;
    }

    private String encodeToBase64(final BufferedImage image) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "JPEG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }

    /**
     * Validates the uploaded image file
     * @param file The MultipartFile to validate
     * @throws IOException if there are issues with file processing
     * @throws IllegalArgumentException if the file is invalid
     */
    private void validateImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("La taille du fichier dépasse 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Type de fichier non supporté. Utilisez JPEG ou PNG");
        }
    
    }
}