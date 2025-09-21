package com.mrb.tests.service;

import com.mrb.tests.dto.PlanetRequest;
import com.mrb.tests.dto.PlanetResponse;
import com.mrb.tests.mapper.PlanetMapper;
import com.mrb.tests.repository.PlanetRepository;
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
