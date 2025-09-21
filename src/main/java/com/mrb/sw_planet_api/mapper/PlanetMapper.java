package com.mrb.sw_planet_api.mapper;

import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import com.mrb.sw_planet_api.model.Planet;

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
