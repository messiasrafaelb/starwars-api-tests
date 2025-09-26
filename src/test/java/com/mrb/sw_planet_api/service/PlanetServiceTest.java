package com.mrb.sw_planet_api.service;

import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import com.mrb.sw_planet_api.mapper.PlanetMapper;
import com.mrb.sw_planet_api.model.Planet;
import com.mrb.sw_planet_api.repository.PlanetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

// @SpringBootTest(classes = PlanetService.class)
@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {

//    @Autowired
    @InjectMocks
    private PlanetService planetService;

//    @MockBean
    @Mock
    private PlanetRepository planetRepository;

    // method name convention = operation_state_return
    @Test
    @DisplayName("create() should return a Planet with same data when given valid input")
    public void createPlanet_WithValidData_ReturnsPlanet() {
//ARRANGE -------------------------------------------------------------------------------
        var planet = new Planet(1L, "name", "climate", "terrain");
        var planetRequest = new PlanetRequest("name", "climate", "terrain");
        // Stub
        when(planetRepository.save(any(Planet.class))).thenReturn(planet);
//ACT ------------------------------------------------------------------------------------
        // sut = system under test, name convention
        PlanetResponse sut = planetService.create(planetRequest);
//ASSERT ---------------------------------------------------------------------------------
        assertThat(sut)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(planet);
        // Spy
        verify(planetRepository).save(any(Planet.class));
        verifyNoMoreInteractions(planetRepository);
    }

    @Test
    @DisplayName("create() should return a RuntimeException with invalid data input")
    public void createPlanet_WithInvalidDate_ThrowsException() {
        var planetRequest = new PlanetRequest("name", "climate", "terrain");
        when(planetRepository.save(any(Planet.class))).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> planetService.create(planetRequest))
                .isInstanceOf(RuntimeException.class);

        verify(planetRepository, times(1)).save(any(Planet.class));
    }

    // Exercise 1
    @Test
    @DisplayName("findById() should return a Planet with valid ID")
    public void findById_WithValidId_ReturnsPlanet() {
        Optional<Planet> planet = Optional.of(new Planet(1L, "name", "climate", "terrain"));
        when(planetRepository.findById(1L)).thenReturn(planet);

        PlanetResponse sut = planetService.findById(1L);

        assertThat(sut)
                .usingRecursiveComparison()
                .isEqualTo(PlanetMapper.toResponse(planet.get()));

        verify(planetRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(planetRepository);
    }

}
