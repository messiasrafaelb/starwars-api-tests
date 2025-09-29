package com.mrb.sw_planet_api.repository;

import com.mrb.sw_planet_api.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlanetRepository extends JpaRepository<Planet, Long>, JpaSpecificationExecutor<Planet> {
    Optional<Planet> findByNameContainingIgnoreCase(String name);
}
