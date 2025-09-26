package com.mrb.sw_planet_api.exception;

public class PlanetNotFoundException extends RuntimeException {
    public PlanetNotFoundException(Long id) {
        super("Planet not found with id " + id);
    }
    public PlanetNotFoundException(String name) {
        super("Planet not found with name " + name);
    }}
