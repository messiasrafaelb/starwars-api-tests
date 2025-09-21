package com.mrb.tests.mapper;

import com.mrb.tests.dto.PlanetRequest;
import com.mrb.tests.dto.PlanetResponse;
import com.mrb.tests.model.Planet;
import org.springframework.stereotype.Component;

import java.util.List;

public class PlanetMapper {

    public static PlanetResponse toResponse(Planet planet) {
        return PlanetResponse.builder()
                .id(planet.getId())
                .name(planet.getName())
                .climate(planet.getClimate())
                .terrain(planet.getTerrain())
                .build();
    }

    public static Planet toEntity(PlanetRequest request) {
        var planet = new Planet();
        planet.setName(request.getName());
        planet.setClimate(request.getClimate());
        planet.setTerrain(request.getTerrain());
        return planet;
    }

    public static List<PlanetResponse> toResponse(List<Planet> planets) {
        return planets.stream()
                .map(PlanetMapper::toResponse)
                .toList();
    }
}
