package com.mrb.tests.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
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
