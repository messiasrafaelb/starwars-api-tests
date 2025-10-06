package com.mrb.sw_planet_api;

import com.mrb.sw_planet_api.dto.PlanetPatchRequest;
import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class StarWarsApplicationIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void createPlanet_ReturnsCreated() {
        var planet = new PlanetRequest("Tatooine", "arid", "desert");
        ResponseEntity<PlanetResponse> sut = testRestTemplate.postForEntity("/planets", planet, PlanetResponse.class);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getName()).isEqualTo("Tatooine");
        assertThat(sut.getBody().getClimate()).isEqualTo("arid");
        assertThat(sut.getBody().getTerrain()).isEqualTo("desert");
    }

    @Test
    void findPlanetById_ReturnsOk() {
        ResponseEntity<PlanetResponse> sut = testRestTemplate.getForEntity("/planets/{id}", PlanetResponse.class, 1L);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getName()).isEqualTo("Tatooine");
        assertThat(sut.getBody().getClimate()).isEqualTo("arid");
        assertThat(sut.getBody().getTerrain()).isEqualTo("desert");
    }

    @Test
    void findPlanetByName_ReturnsOk() {
        ResponseEntity<PlanetResponse> sut = testRestTemplate.getForEntity("/planets/name/{name}", PlanetResponse.class, "Mustafar");
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getName()).isEqualTo("Mustafar");
        assertThat(sut.getBody().getClimate()).isEqualTo("hot");
        assertThat(sut.getBody().getTerrain()).isEqualTo("volcano");
    }

    @Test
    void findPlanetsByFilter_ReturnsOk() {
        record PageResponse<T>(
                List<T> content,
                int number,
                int size,
                int totalPages,
                long totalElements,
                boolean first,
                boolean last,
                boolean empty
        ) {}
        ParameterizedTypeReference<PageResponse<PlanetResponse>> responseType =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<PageResponse<PlanetResponse>> sut = testRestTemplate.exchange(
                "/planets/search?climate={climate}&terrain={terrain}",
                HttpMethod.GET,
                null,
                responseType,
                "temperate", ""
        );
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isNotNull();
        assertThat(sut.getBody().content()).isNotEmpty();
        assertThat(sut.getBody().content().size()).isEqualTo(5);
    }

    @Test
    void deleteById_ReturnsOk() {
        ParameterizedTypeReference<Map<String, String>> responseType =
                new ParameterizedTypeReference<Map<String, String>>() {
                };
        ResponseEntity<Map<String, String>> sut = testRestTemplate.exchange(
                "/planets/{id}", HttpMethod.DELETE, null, responseType, 1L);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isNotNull();
        assertThat(sut.getBody()).containsEntry("message", "Planet deleted successfully");
    }

    @Test
    void patch_ReturnsOk() {
        ResponseEntity<PlanetResponse> sut = testRestTemplate.exchange(
                "/planets/{id}", HttpMethod.PATCH,
                new HttpEntity<>(new PlanetPatchRequest("TEST", "TEST", "TEST")),
                PlanetResponse.class, 3L);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getId()).isEqualTo(3L);
        assertThat(sut.getBody().getName()).isEqualTo("TEST");
        assertThat(sut.getBody().getClimate()).isEqualTo("TEST");
        assertThat(sut.getBody().getTerrain()).isEqualTo("TEST");
    }

    @Test
    void update_ReturnsOk() {
        ResponseEntity<PlanetResponse> sut = testRestTemplate.exchange(
                "/planets/{id}", HttpMethod.PUT,
                new HttpEntity<>(new PlanetRequest("Endor", "temperate", "forest")),
                PlanetResponse.class, 3L);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getId()).isEqualTo(3L);
        assertThat(sut.getBody().getName()).isEqualTo("Endor");
        assertThat(sut.getBody().getClimate()).isEqualTo("temperate");
        assertThat(sut.getBody().getTerrain()).isEqualTo("forest");
    }


}
