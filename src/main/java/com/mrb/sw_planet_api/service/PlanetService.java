package com.mrb.sw_planet_api.service;

import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import com.mrb.sw_planet_api.mapper.PlanetMapper;
import com.mrb.sw_planet_api.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanetService {

    public final PlanetRepository repository;

    public PlanetResponse create(PlanetRequest request) {
        return PlanetMapper.toResponse(repository.save(PlanetMapper.toEntity(request)));
    }
}
