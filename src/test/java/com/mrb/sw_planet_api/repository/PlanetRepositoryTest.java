package com.mrb.sw_planet_api.repository;

import com.mrb.sw_planet_api.dto.PlanetRequest;
import com.mrb.sw_planet_api.mapper.PlanetMapper;
import com.mrb.sw_planet_api.model.Planet;
import com.mrb.sw_planet_api.repository.specification.PlanetSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private PlanetRequest tatooine;
    private PlanetRequest hoth;

    @BeforeEach
    void setup() {
        testEntityManager.clear();
        tatooine = new PlanetRequest("Tatooine", "arid", "desert");
        hoth = new PlanetRequest("Hoth", "frozen", "tundra");
        testEntityManager.persist(PlanetMapper.toEntity(tatooine));
        testEntityManager.persist(PlanetMapper.toEntity(hoth));
        testEntityManager.flush();
    }

    @Test
    @DisplayName("save() should persist a Planet and assign an ID")
    void savePlanet_ShouldPersistAndGenerateId() {
        PlanetRequest dagobah = new PlanetRequest("Dagobah", "murky", "swamp");

        Planet saved = planetRepository.save(PlanetMapper.toEntity(dagobah));

        Planet persisted = testEntityManager.find(Planet.class, saved.getId());

        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getName()).isEqualTo("Dagobah");
        assertThat(persisted.getClimate()).isEqualTo("murky");
        assertThat(persisted.getTerrain()).isEqualTo("swamp");
    }

    @Test
    @DisplayName("save() with null name should throw exception")
    void savePlanet_WithNullName_ShouldThrowException() {
        Planet invalid = new Planet(null, null, "arid", "desert");
        assertThatThrownBy(() -> planetRepository.save(invalid))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase() should return planet when name matches")
    void findByNameContainingIgnoreCase_ShouldReturnPlanet() {
        Optional<Planet> found = planetRepository.findByNameContainingIgnoreCase("tatoo");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Tatooine");
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase() should return empty when name does not match")
    void findByNameContainingIgnoreCase_ShouldReturnEmpty() {
        Optional<Planet> found = planetRepository.findByNameContainingIgnoreCase("nonexistent");
        assertThat(found).isEmpty();
    }


    @Test
    @DisplayName("findAll() should return all persisted planets")
    void findAll_ShouldReturnAllPlanets() {
        List<Planet> planets = planetRepository.findAll();

        assertThat(planets)
                .hasSize(2)
                .extracting(Planet::getName)
                .containsExactlyInAnyOrder("Tatooine", "Hoth");
    }

    @Test
    @DisplayName("findAll() with specification climateContains() should return all persisted planets with 'arid' climate")
    void findAll_WithSpecificationClimate_ShouldReturnPlanets() {
        Specification<Planet> spec = PlanetSpecifications.climateContains("arid");
        List<Planet> planetsFiltered = planetRepository.findAll(spec);

        assertThat(planetsFiltered)
                .hasSize(1)
                .extracting(Planet::getName, Planet::getClimate, Planet::getTerrain)
                .containsExactly(tuple("Tatooine", "arid", "desert"));
    }

    @Test
    @DisplayName("findAll() with specification terrainContains() should return all persisted planets with 'tundra' terrain")
    void findAll_WithSpecificationTerrain_ShouldReturnPlanets() {
        Specification<Planet> spec = PlanetSpecifications.terrainContains("tundra");
        List<Planet> planetsFiltered = planetRepository.findAll(spec);

        assertThat(planetsFiltered)
                .hasSize(1)
                .extracting(Planet::getName, Planet::getClimate, Planet::getTerrain)
                .containsExactly(tuple("Hoth", "frozen", "tundra"));
    }

    @Test
    @DisplayName("findAll() with specification not matching any planet should return empty list")
    void findAll_WithSpecificationNoMatch_ShouldReturnEmpty() {
        Specification<Planet> spec = PlanetSpecifications.climateContains("temperate");
        List<Planet> planetsFiltered = planetRepository.findAll(spec);

        assertThat(planetsFiltered).isEmpty();
    }

    @Test
    @DisplayName("delete() should remove the planet and return empty optional on findById")
    void delete_ShouldReturnOptionalEmpty() {
        PlanetRequest dagobah = new PlanetRequest("Dagobah", "murky", "swamp");
        Planet planet = testEntityManager.persistAndFlush(PlanetMapper.toEntity(dagobah));

        planetRepository.delete(planet);

        Optional<Planet> deletedPlanet = planetRepository.findById(planet.getId());
        assertThat(deletedPlanet).isEmpty();
    }

    @Test
    @DisplayName("delete() with non-existing ID should not throw exception")
    void deleteNonExistingPlanet_ShouldNotFail() {
        assertDoesNotThrow(() -> planetRepository.deleteById(999L));
    }

}
