package com.beautyparlour.repository;

import com.beautyparlour.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    List<Certificate> findByParlourId(UUID parlourId);
    Optional<Certificate> findByIdAndParlourId(UUID id, UUID parlourId);
}
