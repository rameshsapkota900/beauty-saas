package com.beautyparlour.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * File upload service interface for handling images and documents
 */
public interface FileUploadService {
    
    /**
     * Upload service image
     */
    String uploadServiceImage(MultipartFile file, UUID parlourId, UUID serviceId);
    
    /**
     * Upload course image
     */
    String uploadCourseImage(MultipartFile file, UUID parlourId, UUID courseId);
    
    /**
     * Upload staff photo
     */
    String uploadStaffPhoto(MultipartFile file, UUID parlourId, UUID staffId);
    
    /**
     * Upload parlour logo
     */
    String uploadParlourLogo(MultipartFile file, UUID parlourId);
    
    /**
     * Upload certificate image
     */
    String uploadCertificateImage(MultipartFile file, UUID parlourId, UUID certificateId);
    
    /**
     * Upload successful student image
     */
    String uploadSuccessfulStudentImage(MultipartFile file, UUID parlourId, UUID studentId);
    
    /**
     * Delete uploaded file
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * Validate file type and size
     */
    boolean isValidImageFile(MultipartFile file);
    
    /**
     * Get file URL by filename
     */
    String getFileUrl(String filename);
}
