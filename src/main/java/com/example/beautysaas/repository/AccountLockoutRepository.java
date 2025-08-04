package com.example.beautysaas.repository;

import com.example.beautysaas.entity.AccountLockout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountLockoutRepository extends JpaRepository<AccountLockout, UUID> {
    
    Optional<AccountLockout> findByEmail(String email);
    
    @Query("SELECT a FROM AccountLockout a WHERE a.email = :email AND a.isLocked = true AND a.lockedUntil > :now")
    Optional<AccountLockout> findActiveLockedAccount(@Param("email") String email, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE AccountLockout a SET a.isLocked = false, a.lockedUntil = null, a.failedAttempts = 0 WHERE a.email = :email")
    void unlockAccount(@Param("email") String email);
    
    @Modifying
    @Query("UPDATE AccountLockout a SET a.isLocked = false, a.lockedUntil = null, a.failedAttempts = 0 WHERE a.lockedUntil < :now")
    void unlockExpiredAccounts(@Param("now") LocalDateTime now);
}
