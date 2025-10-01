package com.mrb.sw_planet_api.controller;

import com.mrb.sw_planet_api.dto.PlanetPatchRequest;
import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import com.mrb.sw_planet_api.service.PlanetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
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

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlanetResponse> createPlanet(@RequestBody @Valid PlanetRequest request) {
        var planet = service.create(request);
        URI location = URI.create("/planets/" + planet.getId());
        return ResponseEntity.status(CREATED)
                .header(HttpHeaders.LOCATION, location.toString())
                .body(planet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanetResponse> findPlanetById(@PathVariable @Min(1) Long id) {
        var planet = service.findById(id);
        return ResponseEntity.status(OK).body(planet);
    }

    @GetMapping("name/{name}")
    public ResponseEntity<PlanetResponse> findPlanetByName(@PathVariable @NotBlank String name) {
        var planet = service.findByName(name);
        return ResponseEntity.status(OK).body(planet);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PlanetResponse>> findPlanets(
            @PageableDefault(page = 0, size = 15, sort = "title", direction = ASC) Pageable pageable,
            @RequestParam(required = false) String climate, @RequestParam(required = false) String terrain) {
        var planets = service.find(climate, terrain, pageable);
        return ResponseEntity.status(OK).body(planets);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePlanet(@PathVariable @Min(1) Long id) {
        var message = service.delete(id);
        return ResponseEntity.status(OK).body(message);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlanetResponse> partialUpdatePlanet(@PathVariable @Min(1) Long id,
                                                              @RequestBody @Valid @NotNull PlanetPatchRequest request) {
        var planet = service.patch(id, request);
        return ResponseEntity.status(OK).body(planet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanetResponse> updatePlanet(@PathVariable @Min(1) Long id,
                                                       @RequestBody @Valid @NotNull PlanetRequest request) {
        var planet = service.update(id, request);
        return ResponseEntity.status(OK).body(planet);
    }
}
