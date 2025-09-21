package com.mrb.tests.controller;

import com.mrb.tests.dto.PlanetRequest;
import com.mrb.tests.dto.PlanetResponse;
import com.mrb.tests.service.PlanetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/planets")
@RequiredArgsConstructor
@Validated
public class PlanetController {

    private final PlanetService service;

    @PostMapping
    public ResponseEntity<PlanetResponse> create(@RequestBody @Valid PlanetRequest request) {
        var planet = service.create(request);
        return ResponseEntity.status(CREATED).body(planet);
    }
}
