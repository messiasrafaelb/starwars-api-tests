package com.mrb.sw_planet_api.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

public record PlanetPatchRequest(
        @Nullable
        String name,

        @Nullable
        @Size(min = 3, message = "Climate must have at least 3 characters")
        String climate,

        @Nullable
        @Size(min = 3, message = "Terrain must have at least 3 characters")
        String terrain) {}
