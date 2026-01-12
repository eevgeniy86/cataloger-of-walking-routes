package ru.elistratov;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.elistratov.model.domain.Point;
import ru.elistratov.model.domain.RelationsProcessingStatus;
import ru.elistratov.model.domain.Route;
import ru.elistratov.model.domain.Waypoint;

@Testcontainers
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
class RouteTest {
    private static final String POSTGRES_VERSION = "postgres:16";
    private static final String POSTGRES_DB_NAME = "demoDB";
    private static final String POSTGRES_USER = "usr";
    private static final String POSTGRES_PASSWORD = "pwd";

    // Creating DB container and connection properties, static-keyword - for one container for all class
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_VERSION)
            .withDatabaseName(POSTGRES_DB_NAME)
            .withUsername(POSTGRES_USER)
            .withPassword(POSTGRES_PASSWORD);

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder().create();

    @DynamicPropertySource
    private static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop"); // Создавать схему при каждом запуске
    }

    @Test
    @DisplayName("Route saved from url")
    void testSaveRouteFromUrl() throws Exception {
        String url = "https://yandex.ru/maps?mode=routes&rtext=55.620324%2C37.696012~55.623574%2C37.705061&rtt=pd";

        MvcResult response = mockMvc.perform(
                        post("/api/v1/route/url").contentType("text/plain").content(url))
                .andExpect(status().isOk())
                .andReturn();
        Route saved = gson.fromJson(response.getResponse().getContentAsString(), Route.class);
        assertThat(saved.id()).isNotNull();
    }

    @Test
    @DisplayName("Route saved from JSON")
    void testSaveRouteFromJson() throws Exception {
        Waypoint wp1 = new Waypoint(null, (short) 0, null, new Point(null, 55.620324f, 37.696012f), null);
        Waypoint wp2 = new Waypoint(null, (short) 1, null, new Point(null, 55.623574f, 37.705061f), null);
        Route newRoute = new Route(
                null,
                null,
                null,
                (short) 2,
                Set.of(wp1, wp2),
                null,
                null,
                null,
                RelationsProcessingStatus.NOT_PROCESSED);
        String jsonRequest = gson.toJson(newRoute);

        MvcResult response = mockMvc.perform(
                        post("/api/v1/route").contentType("application/json").content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
        Route saved = gson.fromJson(response.getResponse().getContentAsString(), Route.class);
        assertThat(saved.id()).isNotNull();
    }

    @Test
    @DisplayName("Got url for existing route")
    void testGetRouteUrl() throws Exception {
        String expectedUrl = "https://yandex.ru/maps?mode=routes&rtext=55.6203,37.696~55.6236,37.7051&rtt=pd";
        Waypoint wp1 = new Waypoint(null, (short) 0, null, new Point(null, 55.620324f, 37.696012f), null);
        Waypoint wp2 = new Waypoint(null, (short) 1, null, new Point(null, 55.623574f, 37.705061f), null);
        Route newRoute = new Route(
                null,
                null,
                null,
                (short) 2,
                Set.of(wp1, wp2),
                null,
                null,
                null,
                RelationsProcessingStatus.NOT_PROCESSED);
        String jsonRequest = gson.toJson(newRoute);

        MvcResult saveResponse = mockMvc.perform(
                        post("/api/v1/route").contentType("application/json").content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
        long savedId = gson.fromJson(saveResponse.getResponse().getContentAsString(), Route.class)
                .id();

        String actualUrl = mockMvc.perform(
                        get("/api/v1/route/{id}/url", savedId).accept("application/json; charset=utf-8"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(actualUrl).isEqualTo(expectedUrl);
    }
}
