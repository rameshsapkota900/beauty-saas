package com.example.beauty_saas.repository;

import com.example.beautysaas.entity.Role;
import com.example.beautysaas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    long count(); // For dashboard analytics (total users)
    long countByParlourId(UUID parlourId); // For dashboard analytics (parlour-specific users)
    long countByRole(Role role); // For dashboard analytics (users by role)
    long countByParlourIdAndRole(UUID parlourId, Role role); // For dashboard analytics (parlour-specific users by role)
}
