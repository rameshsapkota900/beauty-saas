package com.beautyparlour.repository;

import com.beautyparlour.entity.Parlour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParlourRepository extends JpaRepository<Parlour, UUID> {
}
