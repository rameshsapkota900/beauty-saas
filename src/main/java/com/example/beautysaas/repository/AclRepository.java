package com.example.beautysaas.repository;

import com.example.beautysaas.entity.AccessControlEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface AclRepository extends JpaRepository<AccessControlEntry, UUID> {
    
    boolean existsByUserEmailAndResourceTypeAndResourceIdAndPermission(
        String userEmail, String resourceType, String resourceId, String permission);
        
    Set<AccessControlEntry> findByUserEmailAndResourceTypeAndResourceId(
        String userEmail, String resourceType, String resourceId);
        
    Set<AccessControlEntry> findByResourceTypeAndResourceIdAndPermission(
        String resourceType, String resourceId, String permission);
        
    void deleteByUserEmailAndResourceTypeAndResourceIdAndPermission(
        String userEmail, String resourceType, String resourceId, String permission);
        
    Set<AccessControlEntry> findByUserEmail(String userEmail);
}
