package com.mrb.sw_planet_api.repository;

import com.mrb.sw_planet_api.model.Planet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanetRepository extends JpaRepository<Planet, Long>, Specification<Planet> {
    Optional<Planet> findByNameContainingIgnoreCase(String name);
    Page<Planet> findAll(Specification<Planet> spec, Pageable pageable);
}
