package com.mrb.sw_planet_api.service;

import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.dto.PlanetResponse;
import com.mrb.sw_planet_api.exception.PlanetNotFoundException;
import com.mrb.sw_planet_api.mapper.PlanetMapper;
import com.mrb.sw_planet_api.model.Planet;
import com.mrb.sw_planet_api.repository.PlanetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
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
//ARRANGE ______________________________________________________________________________________________________________
        var planet = new Planet(1L, "name", "climate", "terrain");
        var planetRequest = new PlanetRequest("name", "climate", "terrain");
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
    public void createPlanet_WithInvalidDate_ThrowsException() {
        var planetRequest = new PlanetRequest("name", "climate", "terrain");
        when(planetRepository.save(any(Planet.class))).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> planetService.create(planetRequest))
                .isInstanceOf(RuntimeException.class);

        verify(planetRepository, times(1)).save(any(Planet.class));
    }

    // Exercise 1 ______________________________________________________________________________________________________
    @Test
    @DisplayName("findById() should return a Planet with valid ID")
    public void findById_WithValidId_ReturnsPlanet() {
        Optional<Planet> planet = Optional.of(
                new Planet(1L, "name", "climate", "terrain"));
        when(planetRepository.findById(eq(1L))).thenReturn(planet);

        PlanetResponse sut = planetService.findById(1L);

        assertThat(sut)
                .usingRecursiveComparison()
                .isEqualTo(PlanetMapper.toResponse(planet.get()));

        verify(planetRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById() should return PlanetNotFoundException with invalid ID input")
    public void findById_WithInvalidId_ReturnsPlanetNotFoundException() {
        when(planetRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planetService.findById(1L))
                .isInstanceOf(PlanetNotFoundException.class)
                .hasMessageContaining("Planet not found with id " + 1L);

        verify(planetRepository, times(1)).findById(any(Long.class));
    }

    /*
        @Test
        @DisplayName("findById() should return empty with invalid ID input")
        public void findById_WithInvalidId_ReturnsEmpty() {
            when(planetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

            Optional<PlanetResponse> sut = Optional.ofNullable(planetService.findById(1L));

            assertThat(sut).isEmpty();

            verify(planetRepository, times(1)).findById(any(Long.class));
        }
    */
    // Exercise 2 ______________________________________________________________________________________________________
    @Test
    @DisplayName("findByName() should return a Planet with valid name input")
    public void findByName_WithValidName_ReturnsPlanet() {
        Optional<Planet> planet = Optional.of(
                new Planet(1L, "name", "climate", "terrain"));
        PlanetResponse expected = PlanetMapper.toResponse(planet.get());
        when(planetRepository.findByNameContainingIgnoreCase(eq("name"))).thenReturn(planet);

        PlanetResponse planetResponse = planetService.findByName("name");

        assertThat(planetResponse).usingRecursiveComparison().isEqualTo(expected);

        verify(planetRepository, times(1)).findByNameContainingIgnoreCase(eq("name"));
    }

    @Test
    @DisplayName("findByName() should return PlanetNotFound with name input not found")
    public void findByName_WithNotFoundName_ReturnsPlanetNotFoundException() {
        when(planetRepository.findByNameContainingIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planetService.findByName("name"))
                .isInstanceOf(PlanetNotFoundException.class)
                .hasMessageContaining("Planet not found with name " + "name");

        verify(planetRepository, times(1)).findByNameContainingIgnoreCase(any(String.class));
    }

    // Exercise 3 ______________________________________________________________________________________________________
    @Test
    @DisplayName("find() should return a page of Planet with valid data input")
    public void find_WithValidDataInput_ReturnsPlanets() {
        var planets = List.of(
                new Planet(1L, "1", "1", "1"),
                new Planet(2L, "2", "1", "1"),
                new Planet(3L, "3", "3", "3"));
        PageImpl<Planet> planetPage = new PageImpl<>(planets, Pageable.unpaged(), planets.size());
        when(planetRepository.findAll(any(Pageable.class))).thenReturn(planetPage);

        Page<PlanetResponse> sut = planetService.find(null, null, Pageable.unpaged());

        assertThat(sut.getContent())
                .hasSize(3)
                .extracting("name")
                .containsExactly("1", "2", "3");

        verify(planetRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("find() should return a empty page of Planet with not found data input")
    public void find_WithNotFoundDataInput_ReturnsEmptyPage() {
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
    public void delete_WithValidID_ReturnsVoid() {
        Planet planet = new Planet(1L, "name", "climate", "terrain");
        when(planetRepository.findById(1L)).thenReturn(Optional.of(planet));
        doNothing().when(planetRepository).delete(planet);

        planetService.delete(1L);

        verify(planetRepository, times(1)).findById(1L);
        verify(planetRepository, times(1)).delete(planet);
    }

    @Test
    @DisplayName("delete() should return a PlanetNotFoundException with invalid ID input")
    public void delete_WithIdNotFound_ReturnsPlanetNotFoundException() {
        when(planetRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planetService.delete(99L))
                .isInstanceOf(PlanetNotFoundException.class)
                .hasMessageContaining("Planet not found with id " + 99L);

        verify(planetRepository, times(1)).findById(99L);
        verify(planetRepository, never()).delete(any());
    }




}
