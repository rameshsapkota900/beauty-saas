package com.beautyparlour.service.impl;

import com.beautyparlour.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Local file upload implementation
 * In production, replace with cloud storage service (AWS S3, Google Cloud, etc.)
 */
@Service
public class LocalFileUploadServiceImpl implements FileUploadService {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalFileUploadServiceImpl.class);
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String UPLOAD_DIR = "/uploads/";

    @Override
    public String uploadServiceImage(MultipartFile file, UUID parlourId, UUID serviceId) {
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Invalid image file");
        }
        
        String filename = generateFilename("service", parlourId, serviceId, file.getOriginalFilename());
        String fileUrl = saveFile(file, filename);
        
        logger.info("Service image uploaded: {} for parlour: {}, service: {}", filename, parlourId, serviceId);
        return fileUrl;
    }

    @Override
    public String uploadCourseImage(MultipartFile file, UUID parlourId, UUID courseId) {
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Invalid image file");
        }
        
        String filename = generateFilename("course", parlourId, courseId, file.getOriginalFilename());
        String fileUrl = saveFile(file, filename);
        
        logger.info("Course image uploaded: {} for parlour: {}, course: {}", filename, parlourId, courseId);
        return fileUrl;
    }

    @Override
    public String uploadStaffPhoto(MultipartFile file, UUID parlourId, UUID staffId) {
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Invalid image file");
        }
        
        String filename = generateFilename("staff", parlourId, staffId, file.getOriginalFilename());
        String fileUrl = saveFile(file, filename);
        
        logger.info("Staff photo uploaded: {} for parlour: {}, staff: {}", filename, parlourId, staffId);
        return fileUrl;
    }

    @Override
    public String uploadParlourLogo(MultipartFile file, UUID parlourId) {
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Invalid image file");
        }
        
        String filename = generateFilename("parlour", parlourId, null, file.getOriginalFilename());
        String fileUrl = saveFile(file, filename);
        
        logger.info("Parlour logo uploaded: {} for parlour: {}", filename, parlourId);
        return fileUrl;
    }

    @Override
    public String uploadCertificateImage(MultipartFile file, UUID parlourId, UUID certificateId) {
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Invalid image file");
        }
        
        String filename = generateFilename("certificate", parlourId, certificateId, file.getOriginalFilename());
        String fileUrl = saveFile(file, filename);
        
        logger.info("Certificate image uploaded: {} for parlour: {}, certificate: {}", filename, parlourId, certificateId);
        return fileUrl;
    }

    @Override
    public String uploadSuccessfulStudentImage(MultipartFile file, UUID parlourId, UUID studentId) {
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Invalid image file");
        }
        
        String filename = generateFilename("student", parlourId, studentId, file.getOriginalFilename());
        String fileUrl = saveFile(file, filename);
        
        logger.info("Student image uploaded: {} for parlour: {}, student: {}", filename, parlourId, studentId);
        return fileUrl;
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        logger.info("MOCK DELETE: File deletion requested for: {}", fileUrl);
        // In real implementation, delete the actual file
        return true;
    }

    @Override
    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }
        
        String extension = filename.toLowerCase().substring(filename.lastIndexOf('.'));
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    @Override
    public String getFileUrl(String filename) {
        return UPLOAD_DIR + filename;
    }

    private String generateFilename(String type, UUID parlourId, UUID entityId, String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        if (entityId != null) {
            return String.format("%s_%s_%s_%s%s", type, parlourId, entityId, timestamp, extension);
        } else {
            return String.format("%s_%s_%s%s", type, parlourId, timestamp, extension);
        }
    }

    private String saveFile(MultipartFile file, String filename) {
        // Mock implementation - in production, save to actual file system or cloud storage
        logger.info("MOCK SAVE: File would be saved as: {}", filename);
        return getFileUrl(filename);
    }
}
