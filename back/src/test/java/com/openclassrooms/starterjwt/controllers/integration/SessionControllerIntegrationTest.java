package com.openclassrooms.starterjwt.controllers.integration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.services.SessionService;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SessionController Integration Tests")
public class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionService sessionService;

    private final ObjectMapper mapper = new ObjectMapper();

    private static String jwt;

    @BeforeEach
    public void setUp(@Autowired AuthenticationManager authenticationManager,
            @Autowired JwtUtils jwtUtils) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("yoga@studio.com", "test!1234"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        jwt = jwtUtils.generateJwtToken(authentication);
    }

    ;

    @Test
    @DisplayName("Find Session by ID → Returns Session")
    public void testFindSessionById() throws Exception {
        Session session = Session.builder()
                .name("Session")
                .date(new Date())
                .description("Session description")
                .teacher(Teacher.builder().id(1L).firstName("FirstName").lastName("LastName").build())
                .build();
        session = sessionService.create(session);

        mockMvc.perform(get("/api/session/{id}", session.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Session"))
                .andExpect(jsonPath("$.description").value("Session description"));
    }

    ;

    @Test
    @DisplayName("Find Session by ID → Returns Null for Non-Existing ID")
    public void testFindSessionById_NonExistingId() throws Exception {
        mockMvc.perform(get("/api/session/{id}", 999L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    ;

    @Test
    @DisplayName("Find All Sessions → Returns All Sessions")
    public void testFindAllSession() throws Exception {
        mockMvc.perform(get("/api/session")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    ;

    @Test
    @DisplayName("Create Session → Saves and Returns Session")
    public void testCreateSession() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("New Session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("New Description");

        mockMvc.perform(post("/api/session")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Session"))
                .andExpect(jsonPath("$.description").value("New Description"));
    }

    ;

    @Test
    @DisplayName("Update Session → Returns Updated Session")
    public void testUpdateSession() throws Exception {
        Session session = Session.builder()
                .name("Session")
                .date(new Date())
                .description("Session description")
                .teacher(Teacher.builder().id(1L).firstName("FirstName").lastName("LastName").build())
                .build();
        session = sessionService.create(session);

        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated Session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("Updated Description");

        mockMvc.perform(put("/api/session/{id}", session.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Session"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    ;

    @Test
    @DisplayName("Delete Session by ID")
    public void testDeleteSession() throws Exception {
        Session session = Session.builder()
                .name("Session")
                .date(new Date())
                .description("Session description")
                .teacher(Teacher.builder().id(1L).firstName("FirstName").lastName("LastName").build())
                .build();
        session = sessionService.create(session);

        mockMvc.perform(delete("/api/session/{id}", session.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/session/{id}", session.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNotFound());
    }

    ;

    @Test
    @DisplayName("Participate in Session → Successful Participation")
    public void testParticipateSession() throws Exception {
        Session session = Session.builder()
                .name("Session")
                .date(new Date())
                .description("Session description")
                .users(new ArrayList<>())
                .build();
        session = sessionService.create(session);

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", session.getId(), 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    ;

    @Test
    @DisplayName("Participate in Session → User Not Found")
    public void testParticipateSession_UserNotFound() throws Exception {
        Session session = Session.builder()
                .name("Session")
                .date(new Date())
                .description("Session description")
                .teacher(Teacher.builder().id(1L).firstName("FirstName").lastName("LastName").build())
                .build();
        session = sessionService.create(session);

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", session.getId(), 999L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNotFound());
    }

    ;

    @Test
    @DisplayName("Participate in Session → Session Not Found")
    public void testParticipateSession_SessionNotFound() throws Exception {
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", 999L, 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNotFound());
    }

    ;

    @Test
    @DisplayName("No Longer Participate in Session → Successful")
    public void testNoLongerParticipateSession() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("john.doe@example.fr")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(true)
                .build();
        Session session = Session.builder()
                .name("Session")
                .date(new Date())
                .description("Session description")
                .users(List.of(user))
                .teacher(Teacher.builder().id(1L).firstName("Bob").lastName("Marley").build())
                .build();
        session = sessionService.create(session);

        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", session.getId(), 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    ;

    @Test
    @DisplayName("No Longer Participate in Session → Session Not Found")
    public void testNoLongerParticipateSession_SessionNotFound() throws Exception {
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", 999L, 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNotFound());
    }
;
}
