package com.example.beautysaas.service;

import com.example.beautysaas.config.AuditRetentionConfig;
import com.example.beautysaas.entity.AuditTrail;
import com.example.beautysaas.repository.AuditTrailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "audit.retention.enabled", havingValue = "true", matchIfMissing = true)
public class AuditRetentionService {

    private final AuditTrailRepository auditTrailRepository;
    private final EntityManager entityManager;
    private final AuditRetentionConfig config;
    
    @Scheduled(cron = "${audit.retention.schedule:0 0 1 * * ?}")
    @Transactional
    public void cleanupOldAuditLogs() {
        try {
            LocalDateTime retentionDate = LocalDateTime.now()
                .minusMonths(config.getRetentionMonths());
            
            // Use batch processing to avoid memory issues with large deletions
            int deleted;
            
            do {
                // Delete in batches using native query for better performance
                deleted = entityManager.createQuery(
                        "DELETE FROM AuditTrail a WHERE a.createdAt < :retentionDate AND " +
                        "a.eventType NOT IN ('SECURITY_WARNING', 'RISK_ASSESSMENT')")
                    .setParameter("retentionDate", retentionDate)
                    .setMaxResults(config.getBatchSize())
                    .executeUpdate();
                
                entityManager.flush();
                entityManager.clear();
                
                if (deleted > 0) {
                    log.info("Deleted {} audit logs older than {}", deleted, retentionDate);
                }
            } while (deleted > 0);
            
            // Archive critical security events instead of deleting them
            archiveCriticalEvents(retentionDate);
            
        } catch (Exception e) {
            log.error("Error during audit log cleanup", e);
            throw e;
        }
    }
    
    @Transactional
    public void archiveCriticalEvents(LocalDateTime cutoffDate) {
        // Implementation could archive to a separate table, external system, or compressed storage
        log.info("Archiving critical security events before {}", cutoffDate);
        // TODO: Implement archiving logic based on business requirements
    }
}
