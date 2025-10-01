package com.mrb.sw_planet_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrb.sw_planet_api.dto.PlanetPatchRequest;
import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import com.mrb.sw_planet_api.exception.PlanetNotFoundException;
import com.mrb.sw_planet_api.service.PlanetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;
import java.util.Map;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanetService planetService;

    @Autowired
    private ObjectMapper objectMapper;

    private PlanetRequest request;
    private PlanetResponse response;
    private PlanetPatchRequest patchRequest;
    private final String URI = "/planets";

    @BeforeEach
    void setup() {
        request = new PlanetRequest("Tatooine", "arid", "desert");
        response = new PlanetResponse(1L, "Tatooine", "arid", "desert");
        patchRequest = new PlanetPatchRequest("Tatooine", "arid", "desert");
    }

    @Test
    @DisplayName("POST /planets should create a new planet and return 201")
    void createPlanet_ShouldReturnCreated() throws Exception {
        when(planetService.create(any(PlanetRequest.class))).thenReturn(response);
/*
        mockMvc.perform(MockMvcRequestBuilders.post("/planets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Tatooine",
                                    "climate":"murky",
                                    "terrain":"swamp"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tatooine"))
                .andDo(MockMvcResultHandlers.print());
*/
        ResultActions result = mockMvc.perform(post("/planets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/planets/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tatooine"))
                .andDo(print());

        verify(planetService, times(1)).create(any(PlanetRequest.class));
        verifyNoMoreInteractions(planetService);
    }
//    MockMvc is a class that simulates expected HTTP requests, your main components are:
//    MockMvc = entrypoint, responsible for executes the request mock
//    MockMvcRequestBuilders = utility class for create requests
//    MockMvcResultActions = is the object received after execute mockMvc.perform(...),
//    -      allow execute verifications/assertions (.andExpect...) and some actions (.andDo...)
//    MockMvcResultMatchers = helpers for validate the response result,
//    -      status(...), header(...), content(...), jsonPath(...)
//    MockMvcResultHandlers = helpers to add actions in MockMvcResultActions (logging e.g andDo(print()), debugging, etc.)
//    MvcResult = represent the complete response, util for manually inspection
//    -      MockMvc result = mockMvc.perform(MockMvcRequestBuilders.get("/planets/1)).andReturn();


    @Test
    @DisplayName("POST /planets should return 400")
    void createPlanet_ShouldReturnBadRequest() throws Exception {
        request.setName(null);
        request.setClimate(null);
        request.setTerrain(null);
        mockMvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /planets/{id} should return a planet and 200")
    void findPlanetById_ShouldReturnOk() throws Exception {
        when(planetService.findById(anyLong())).thenReturn(response);
        mockMvc.perform(get(URI + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tatooine"))
                .andExpect(jsonPath("$.climate").value("arid"))
                .andExpect(jsonPath("$.terrain").value("desert"))
                .andDo(print());
        verify(planetService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("GET /planets/{id} should return 404")
    void findPlanetById_ShouldReturnNotFound() throws Exception {
        when(planetService.findById(anyLong()))
                .thenThrow(new PlanetNotFoundException(99L));
        mockMvc.perform(get(URI + "/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Planet not found with id 99"))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andDo(print());
        verify(planetService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("GET /planets/name/{name} should return 200")
    void findPlanetByName_ShouldReturnOk() throws Exception {
        when(planetService.findByName(anyString())).thenReturn(response);
        mockMvc.perform(get(URI + "/name/tatooine")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Tatooine"))
                .andExpect(jsonPath("$.climate").value("arid"))
                .andExpect(jsonPath("$.terrain").value("desert"))
                .andDo(print());
        verify(planetService, times(1)).findByName(anyString());
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("GET /planets/name/{name} should return 404")
    void findPlanetByName_ShouldReturnNotFound() throws Exception {
        when(planetService.findByName(anyString()))
                .thenThrow(new PlanetNotFoundException("neymar"));
        mockMvc.perform(get(URI + "/name/neymar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Planet not found with name neymar"))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andDo(print());
        verify(planetService, times(1)).findByName(anyString());
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("GET /planets should return 200")
    void findPlanets_ShouldReturnOk() throws Exception {
        List<PlanetResponse> planets = List.of(response);
        PageImpl<PlanetResponse> planetPage = new PageImpl<>(planets, Pageable.unpaged(), planets.size());
        when(planetService.find(anyString(), anyString(), any(Pageable.class))).thenReturn(planetPage);
        mockMvc.perform(get(URI + "/search?climate=arid&terrain=desert")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].name").value("Tatooine"))
                .andExpect(jsonPath("$.content[0].climate").value("arid"))
                .andExpect(jsonPath("$.content[0].terrain").value("desert"))
                .andDo(print());
        verify(planetService, times(1)).find(anyString(), anyString(), any(Pageable.class));
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("GET /planets should return 200 when empty page")
    void findPlanets_ShouldReturnBadRequest() throws Exception {
        PageImpl<PlanetResponse> emptyPage = new PageImpl<>(List.of(), Pageable.unpaged(), 0);
        when(planetService.find(anyString(), anyString(), any(Pageable.class))).thenReturn(emptyPage);
        mockMvc.perform(get(URI + "/search?climate=unknown&terrain=unknown")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
        verify(planetService, times(1)).find(anyString(), anyString(), any(Pageable.class));
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("DELETE planets/{id} should return 200")
    void deletePlanet_ShouldReturnOk() throws Exception {
        when(planetService.delete(anyLong())).thenReturn(Map.of("message", "Planet deleted successfully"));
        mockMvc.perform(delete(URI + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Planet deleted successfully"))
                .andDo(print());
        verify(planetService, times(1)).delete(1L);
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("DELETE planets/{id} should return 404")
    void deletePlanet_ShouldReturnNotFound() throws Exception {
        when(planetService.delete(anyLong())).thenThrow(new PlanetNotFoundException(99L));
        mockMvc.perform(delete(URI + "/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Planet not found with id 99"))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andDo(print());
        verify(planetService, times(1)).delete(99L);
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("PATCH planets/{id} should return 200")
    void partialUpdatePlanet_ShouldReturnOk() throws Exception {
        when(planetService.patch(1L, patchRequest)).thenReturn(response);
        mockMvc.perform(patch(URI + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Tatooine"))
                .andExpect(jsonPath("$.climate").value("arid"))
                .andExpect(jsonPath("$.terrain").value("desert"))
                .andDo(print());
        verify(planetService, times(1)).patch(1L, patchRequest);
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("PATCH planets/{id} should return 404")
    void partialUpdatePlanet_ShouldReturnNotFound() throws Exception {
        when(planetService.patch(anyLong(), any(PlanetPatchRequest.class)))
                .thenThrow(new PlanetNotFoundException(99L));
        mockMvc.perform(patch(URI + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Planet not found with id 99"))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andDo(print());
        verify(planetService, times(1)).patch(anyLong(), any(PlanetPatchRequest.class));
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("UPDATE planets/{id} should return 200")
    void updatePlanet_ShouldReturnOk() throws Exception {
        when(planetService.update(anyLong(), any(PlanetRequest.class))).thenReturn(response);
        mockMvc.perform(put(URI + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Tatooine"))
                .andExpect(jsonPath("$.climate").value("arid"))
                .andExpect(jsonPath("$.terrain").value("desert"))
                .andDo(print());
        verify(planetService, times(1)).update(anyLong(), any(PlanetRequest.class));
        verifyNoMoreInteractions(planetService);
    }

    @Test
    @DisplayName("UPDATE planets/{id} should return 400")
    void updatePlanet_ShouldReturnNotFound() throws Exception {
        when(planetService.update(anyLong(), any(PlanetRequest.class)))
                .thenThrow(new HttpMessageNotReadableException(""));
        mockMvc.perform(put(URI + "/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andDo(print());
    }
}
