package com.example.beautysaas.repository;

import com.example.beautysaas.entity.PasswordPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordPolicyRepository extends JpaRepository<PasswordPolicy, Long> {
    
    PasswordPolicy findFirstByOrderByUpdatedAtDesc();
}
