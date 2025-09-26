package com.mrb.sw_planet_api.repository.specification;

import com.mrb.sw_planet_api.model.Planet;
import com.mrb.sw_planet_api.model.Planet_;
import org.springframework.data.jpa.domain.Specification;

public class PlanetSpecifications {

    public static Specification<Planet> climateContains(String climate) {
        return ((root, query, cb)
                -> cb.like(cb.lower(root.get(Planet_.climate)), "%" + climate.toLowerCase() + "%"));
    }

    public static Specification<Planet> terrainContains(String terrain) {
        return ((root, query, cb)
                -> cb.like(cb.lower(root.get(Planet_.terrain)), "%" + terrain.toLowerCase() + "%"));
    }

}
