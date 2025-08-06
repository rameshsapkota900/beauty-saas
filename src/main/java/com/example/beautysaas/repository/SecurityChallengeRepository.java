package com.example.beautysaas.repository;

import com.example.beautysaas.entity.SecurityChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityChallengeRepository extends JpaRepository<SecurityChallenge, Long> {

    Optional<SecurityChallenge> findByVerificationToken(String token);

    Optional<SecurityChallenge> findByEmailAndChallengeTypeAndIsCompletedFalseAndExpiresAtAfter(
            String email,
            SecurityChallenge.ChallengeType challengeType,
            LocalDateTime now);

    @Query("SELECT c FROM SecurityChallenge c WHERE c.email = :email AND c.isCompleted = false " +
           "AND c.expiresAt > :now ORDER BY c.createdAt DESC")
    List<SecurityChallenge> findActiveByEmail(@Param("email") String email, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(c) > 0 FROM SecurityChallenge c WHERE c.email = :email " +
           "AND c.challengeType = :type AND c.isCompleted = false AND c.expiresAt > :now")
    boolean hasActiveChallengeOfType(
            @Param("email") String email,
            @Param("type") SecurityChallenge.ChallengeType type,
            @Param("now") LocalDateTime now);

    @Query("SELECT c FROM SecurityChallenge c WHERE c.isCompleted = false " +
           "AND c.expiresAt <= :expiryTime")
    List<SecurityChallenge> findExpiredChallenges(@Param("expiryTime") LocalDateTime expiryTime);
}
