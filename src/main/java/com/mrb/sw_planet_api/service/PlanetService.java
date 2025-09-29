package com.mrb.sw_planet_api.service;

import com.mrb.sw_planet_api.dto.PlanetPatchRequest;
import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import com.mrb.sw_planet_api.exception.PlanetNotFoundException;
import com.mrb.sw_planet_api.mapper.PlanetMapper;
import com.mrb.sw_planet_api.model.Planet;
import com.mrb.sw_planet_api.repository.PlanetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.Map;
import static com.mrb.sw_planet_api.repository.specification.PlanetSpecifications.climateContains;
import static com.mrb.sw_planet_api.repository.specification.PlanetSpecifications.terrainContains;
import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class PlanetService {

    public final PlanetRepository repository;

    public PlanetResponse create(PlanetRequest request) {
        return PlanetMapper.toResponse(
                repository.save(PlanetMapper.toEntity(request)));
    }

    public PlanetResponse findById(Long id) {
        var planet = repository.findById(id)
                .orElseThrow(() -> new PlanetNotFoundException(id));
        return PlanetMapper.toResponse(planet);
    }

    public PlanetResponse findByName(String name) {
        var planet = repository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new PlanetNotFoundException(name));
        return PlanetMapper.toResponse(planet);
    }

    public Page<PlanetResponse> find(String climate, String terrain, Pageable pageable) {
        Specification<Planet> spec = null;
        if (hasText(climate)) spec = climateContains(climate);
        if (hasText(terrain)) spec = (spec == null) ? terrainContains(terrain) : spec.and(terrainContains(terrain));
        if (spec == null) {
            return repository.findAll(pageable).map(PlanetMapper::toResponse);
        }
        return repository.findAll(spec, pageable).map(PlanetMapper::toResponse);
    }

    public Map<String, String> delete(Long id) {
        var planet = repository.findById(id)
                .orElseThrow(() -> new PlanetNotFoundException(id));
        repository.delete(planet);
        return Map.of("message", "Planet deleted successfully");
    }

    @Transactional
    public PlanetResponse patch(Long id, PlanetPatchRequest request) {
        var planet = repository.findById(id)
                .orElseThrow(() -> new PlanetNotFoundException(id));
        if (hasText(request.name())) planet.setName(request.name());
        if (hasText(request.climate())) planet.setClimate(request.climate());
        if (hasText(request.terrain())) planet.setTerrain(request.terrain());
        return PlanetMapper.toResponse(planet);
    }
//    @Transactional ensure active context JPA and avoid redundant queries

    @Transactional
    public PlanetResponse update(Long id, PlanetRequest request) {
        var planet = repository.findById(id)
                .orElseThrow(() -> new PlanetNotFoundException(id));
        planet.setName(request.getName());
        planet.setClimate(request.getClimate());
        planet.setTerrain(request.getTerrain());
        return PlanetMapper.toResponse(planet);
    }
}
