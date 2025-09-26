package com.mrb.sw_planet_api.controller;

import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import com.mrb.sw_planet_api.service.PlanetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/planets")
@RequiredArgsConstructor
@Validated
public class PlanetController {

    private final PlanetService service;

    @PostMapping
    public ResponseEntity<PlanetResponse> createPlanet(@RequestBody @Valid PlanetRequest request) {
        var planet = service.create(request);
        return ResponseEntity.status(CREATED).body(planet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanetResponse> findPlanetById(@PathVariable @Min(1) Long id) {
        var planet = service.findById(id);
        return ResponseEntity.status(OK).body(planet);
    }

    @GetMapping("/{name}")
    public ResponseEntity<PlanetResponse> findPlanetByName(@PathVariable @NotBlank String name) {
        var planet = service.findByName(name);
        return ResponseEntity.status(OK).body(planet);
    }

    @GetMapping
    public ResponseEntity<Page<PlanetResponse>> findPlanets(
            @PageableDefault(page = 0, size = 15, sort = "title", direction = ASC) Pageable pageable,
            String climate, String terrain) {
        var planets = service.find(climate, terrain, pageable);
        return ResponseEntity.status(OK).body(planets);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deletePlanet(@PathVariable @Min(1) Long id) {
        var message = service.delete(id);
        return ResponseEntity.status(OK).body(message);
    }

}
