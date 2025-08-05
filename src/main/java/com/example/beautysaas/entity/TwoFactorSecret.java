package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "two_factor_secrets")
public class TwoFactorSecret {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String secret;
    
    @Column(nullable = false)
    private boolean enabled;
    
    @ElementCollection
    @CollectionTable(name = "two_factor_backup_codes", 
                    joinColumns = @JoinColumn(name = "two_factor_secret_id"))
    @Column(name = "backup_code")
    private List<String> backupCodes;
}
