package com.example.beautysaas.repository;

import com.example.beautysaas.entity.TwoFactorSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TwoFactorSecretRepository extends JpaRepository<TwoFactorSecret, UUID> {
    Optional<TwoFactorSecret> findByEmail(String email);
}
