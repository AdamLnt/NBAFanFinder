package com.NBAFanFinder.Backend.integration.controllers;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.annotation.PostConstruct;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Tests d'intégration - TeamController (endpoint public)")
public class TeamControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @PostConstruct
    void initMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("GET /api/teams est accessible sans authentification et renvoie les équipes triées par ville")
    void shouldReturnSeededTeamsSortedByVille() throws Exception {
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                // Les 30 équipes NBA sont injectées au démarrage par NbaTeamsSeeder
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(30)))
                // Tri par ville ascendant : "Atlanta" arrive en premier
                .andExpect(jsonPath("$[0].ville").value("Atlanta"))
                .andExpect(jsonPath("$[0].nom").value("Atlanta Hawks"));
    }
}
