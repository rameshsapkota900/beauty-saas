package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "audit_trails")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private AuditEventType eventType;

    @Column(name = "action_type")
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(name = "event_severity")
    @Enumerated(EnumType.STRING)
    private EventSeverity severity;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "event_details", columnDefinition = "TEXT")
    private String eventDetails;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(name = "related_events")
    private String relatedEvents;

    public enum AuditEventType {
        USER_LOGIN,
        USER_LOGOUT,
        PASSWORD_CHANGE,
        PROFILE_UPDATE,
        SECURITY_CHALLENGE,
        RISK_ASSESSMENT,
        PERMISSION_CHANGE,
        DATA_ACCESS,
        CONFIGURATION_CHANGE,
        ADMIN_ACTION,
        SYSTEM_EVENT,
        SECURITY_WARNING
    }

    public enum ActionType {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        LOGIN,
        LOGOUT,
        VERIFY,
        APPROVE,
        REJECT,
        LOCK,
        UNLOCK,
        ENABLE,
        DISABLE
    }

    public enum EventSeverity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }

    public enum EventStatus {
        SUCCESS,
        FAILURE,
        PENDING,
        BLOCKED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Set metadata as a Map
     */
    public void setMetadataMap(Map<String, Object> metadataMap) {
        this.metadata = metadataMap != null ? metadataMap.toString() : null;
    }

    /**
     * Add related event ID
     */
    public void addRelatedEvent(Long eventId) {
        if (this.relatedEvents == null || this.relatedEvents.isEmpty()) {
            this.relatedEvents = eventId.toString();
        } else {
            this.relatedEvents += "," + eventId;
        }
    }
}
