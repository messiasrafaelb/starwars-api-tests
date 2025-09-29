package com.mrb.sw_planet_api.service;

import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import com.mrb.sw_planet_api.exception.PlanetNotFoundException;
import com.mrb.sw_planet_api.mapper.PlanetMapper;
import com.mrb.sw_planet_api.model.Planet;
import com.mrb.sw_planet_api.repository.PlanetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

// @SpringBootTest(classes = PlanetService.class)
@ExtendWith(MockitoExtension.class)
class PlanetServiceTest {
    //    @MockBean
    @Mock
    private PlanetRepository planetRepository;

    //    @Autowired
    @InjectMocks
    private PlanetService planetService;

    private Planet planet;
    private PlanetRequest planetRequest;
    private PlanetResponse planetResponse;

    @BeforeEach
    void setup() {
        planet = new Planet(1L, "name", "climate", "terrain");
        planetRequest = new PlanetRequest("name", "climate", "terrain");
        planetResponse = PlanetMapper.toResponse(planet);
    }

    // method name convention = operation_state_return
    @Test
    @DisplayName("create() should return a PlanetResponse with same data when given valid input")
    void createPlanet_WithValidData_ReturnsPlanetResponse() {
//ARRANGE ______________________________________________________________________________________________________________
        // Stub
        when(planetRepository.save(any(Planet.class))).thenReturn(planet);
//ACT __________________________________________________________________________________________________________________
        // sut = system under test, name convention for the class variable that we are testing.
        PlanetResponse sut = planetService.create(planetRequest);
//ASSERT _______________________________________________________________________________________________________________
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
    void createPlanet_WithInvalidDate_ThrowsRuntimeException() {
        when(planetRepository.save(any(Planet.class))).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> planetService.create(planetRequest))
                .isInstanceOf(RuntimeException.class);

        verify(planetRepository, times(1)).save(any(Planet.class));
    }

    // Exercise 1 ______________________________________________________________________________________________________
    @Test
    @DisplayName("findById() should return a Planet with valid ID")
    void findById_WithValidId_ReturnsPlanetResponse() {
        when(planetRepository.findById(eq(1L))).thenReturn(Optional.of(planet));

        PlanetResponse sut = planetService.findById(1L);

        assertThat(sut)
                .usingRecursiveComparison()
                .isEqualTo(planetResponse);

        verify(planetRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById() should return PlanetNotFoundException with invalid ID input")
    void findById_WithInvalidId_ReturnsPlanetNotFoundException() {
        when(planetRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planetService.findById(1L))
                .isInstanceOf(PlanetNotFoundException.class)
                .hasMessageContaining("Planet not found with id " + 1L);

        verify(planetRepository, times(1)).findById(any(Long.class));
    }

    /*
        @Test
        @DisplayName("findById() should return empty with invalid ID input")
        void findById_WithInvalidId_ReturnsEmpty() {
            when(planetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

            Optional<PlanetResponse> sut = Optional.ofNullable(planetService.findById(1L));

            assertThat(sut).isEmpty();

            verify(planetRepository, times(1)).findById(any(Long.class));
        }
    */
    // Exercise 2 ______________________________________________________________________________________________________
    @Test
    @DisplayName("findByName() should return a PlanetResponse with valid name input")
    void findByName_WithValidName_ReturnsPlanetResponse() {
        when(planetRepository.findByNameContainingIgnoreCase(eq("name"))).thenReturn(Optional.of(planet));

        PlanetResponse planetResponse = planetService.findByName("name");

        assertThat(planetResponse).usingRecursiveComparison().isEqualTo(planet);

        verify(planetRepository, times(1)).findByNameContainingIgnoreCase(eq("name"));
    }

    @Test
    @DisplayName("findByName() should return PlanetNotFoundException with name input not found")
    void findByName_WithNotFoundName_ReturnsPlanetNotFoundException() {
        when(planetRepository.findByNameContainingIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planetService.findByName("name"))
                .isInstanceOf(PlanetNotFoundException.class)
                .hasMessageContaining("Planet not found with name " + "name");

        verify(planetRepository, times(1)).findByNameContainingIgnoreCase(any(String.class));
    }

    // Exercise 3 ______________________________________________________________________________________________________
    @Test
    @DisplayName("find() should return a page of PlanetResponse with valid data input")
    void find_WithValidDataInput_ReturnsPlanetsResponse() {
        var planets = List.of(
                new Planet(1L, "Tatooine", "arid", "desert"));
        PageImpl<Planet> planetPage = new PageImpl<>(planets, Pageable.unpaged(), planets.size());

        when(planetRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(planetPage);

        Page<PlanetResponse> sut = planetService.find("arid", "desert", Pageable.unpaged());

        assertThat(sut.getContent())
                .hasSize(1)
                .extracting(PlanetResponse::getName)
                .containsExactly("Tatooine");

        verify(planetRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("find() should return a empty page of Planet with not found data input")
    void find_WithNotFoundDataInput_ReturnsEmptyPage() {
        List<Planet> planets = List.of();
        PageImpl<Planet> emptyPlanetPage = new PageImpl<>(planets, Pageable.unpaged(), 0);
        when(planetRepository.findAll(any(Pageable.class))).thenReturn(emptyPlanetPage);

        Page<PlanetResponse> sut = planetService.find(null, null, Pageable.unpaged());

        assertThat(sut.getContent()).isEmpty();
        assertThat(sut.getTotalElements()).isEqualTo(0);

        verify(planetRepository, times(1)).findAll(any(Pageable.class));
    }

    // Exercise 4 ______________________________________________________________________________________________________
    @Test
    @DisplayName("delete() should return void with valid ID input")
    void delete_WithValidID_ReturnsVoid() {
        when(planetRepository.findById(1L)).thenReturn(Optional.of(planet));
        doNothing().when(planetRepository).delete(planet);

        Map<String, String > message = planetService.delete(1L);

        assertThat(message).isEqualTo(Map.of("message", "Planet deleted successfully"));

        verify(planetRepository, times(1)).findById(1L);
        verify(planetRepository, times(1)).delete(planet);
    }

    @Test
    @DisplayName("delete() should return a PlanetNotFoundException with invalid ID input")
    void delete_WithIdNotFound_ReturnsPlanetNotFoundException() {
        when(planetRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planetService.delete(99L))
                .isInstanceOf(PlanetNotFoundException.class)
                .hasMessageContaining("Planet not found with id " + 99L);

        verify(planetRepository, times(1)).findById(99L);
        verify(planetRepository, never()).delete(any(Planet.class));
    }
}
