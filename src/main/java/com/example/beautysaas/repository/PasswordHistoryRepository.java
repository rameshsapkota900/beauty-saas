package com.example.beautysaas.repository;

import com.example.beautysaas.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, UUID> {
    
    List<PasswordHistory> findByEmailOrderByCreatedAtDesc(String email);
    
    Optional<PasswordHistory> findFirstByEmailOrderByCreatedAtDesc(String email);
    
    void deleteByCreatedAtBefore(LocalDateTime date);
}
