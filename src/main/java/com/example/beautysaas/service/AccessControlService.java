package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccessControlService {
    
    private final AclRepository aclRepository;
    private final SecurityEventNotifierService eventNotifier;
    
    /**
     * Grant permission to a resource
     */
    @Transactional
    public void grantPermission(String userEmail, String resourceType, String resourceId, String permission) {
        AccessControlEntry ace = AccessControlEntry.builder()
                .userEmail(userEmail)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .permission(permission)
                .build();
                
        aclRepository.save(ace);
        log.info("Granted permission {} on {}/{} to {}", permission, resourceType, resourceId, userEmail);
        eventNotifier.notifySecurityEvent("PERMISSION_GRANTED", userEmail, 
            String.format("Permission %s granted on %s/%s", permission, resourceType, resourceId));
    }
    
    /**
     * Revoke permission from a resource
     */
    @Transactional
    public void revokePermission(String userEmail, String resourceType, String resourceId, String permission) {
        aclRepository.deleteByUserEmailAndResourceTypeAndResourceIdAndPermission(
            userEmail, resourceType, resourceId, permission);
            
        log.info("Revoked permission {} on {}/{} from {}", permission, resourceType, resourceId, userEmail);
        eventNotifier.notifySecurityEvent("PERMISSION_REVOKED", userEmail,
            String.format("Permission %s revoked on %s/%s", permission, resourceType, resourceId));
    }
    
    /**
     * Check if user has specific permission
     */
    public boolean hasPermission(String userEmail, String resourceType, String resourceId, String permission) {
        return aclRepository.existsByUserEmailAndResourceTypeAndResourceIdAndPermission(
            userEmail, resourceType, resourceId, permission);
    }
    
    /**
     * Get all permissions for a user on a resource
     */
    public Set<String> getUserPermissions(String userEmail, String resourceType, String resourceId) {
        return aclRepository.findByUserEmailAndResourceTypeAndResourceId(userEmail, resourceType, resourceId)
                .stream()
                .map(AccessControlEntry::getPermission)
                .collect(Collectors.toSet());
    }
    
    /**
     * Get all users with a specific permission on a resource
     */
    public Set<String> getUsersWithPermission(String resourceType, String resourceId, String permission) {
        return aclRepository.findByResourceTypeAndResourceIdAndPermission(resourceType, resourceId, permission)
                .stream()
                .map(AccessControlEntry::getUserEmail)
                .collect(Collectors.toSet());
    }
    
    /**
     * Check if user has any of the required permissions
     */
    public boolean hasAnyPermission(String userEmail, String resourceType, String resourceId, Set<String> permissions) {
        return permissions.stream()
                .anyMatch(permission -> hasPermission(userEmail, resourceType, resourceId, permission));
    }
    
    /**
     * Check if user has all of the required permissions
     */
    public boolean hasAllPermissions(String userEmail, String resourceType, String resourceId, Set<String> permissions) {
        return permissions.stream()
                .allMatch(permission -> hasPermission(userEmail, resourceType, resourceId, permission));
    }
    
    /**
     * Transfer all permissions from one user to another
     */
    @Transactional
    public void transferPermissions(String fromEmail, String toEmail) {
        Set<AccessControlEntry> entries = aclRepository.findByUserEmail(fromEmail);
        
        entries.forEach(entry -> {
            AccessControlEntry newEntry = AccessControlEntry.builder()
                    .userEmail(toEmail)
                    .resourceType(entry.getResourceType())
                    .resourceId(entry.getResourceId())
                    .permission(entry.getPermission())
                    .build();
            
            aclRepository.save(newEntry);
        });
        
        log.info("Transferred all permissions from {} to {}", fromEmail, toEmail);
        eventNotifier.notifySecurityEvent("PERMISSIONS_TRANSFERRED", toEmail,
            String.format("All permissions transferred from %s", fromEmail));
    }
}
