package com.mrb.sw_planet_api.dto;

import lombok.*;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Builder
@ToString
@Relation(collectionRelation = "planets")
public class PlanetResponse {
    private final Long id;
    private final String name;
    private final String climate;
    private final String terrain;
}
