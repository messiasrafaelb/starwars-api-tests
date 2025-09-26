package com.mrb.sw_planet_api.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String path;
    private String message;
    private List<String> messages;
    private Map<String, List<String>> fieldErrors;
}
