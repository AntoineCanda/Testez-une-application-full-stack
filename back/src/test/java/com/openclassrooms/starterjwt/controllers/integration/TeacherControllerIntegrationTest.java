package com.openclassrooms.starterjwt.controllers.integration;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TeacherController Integration Tests")
public class TeacherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static String jwt;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("yoga@studio.com", "test!1234"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        jwt = jwtUtils.generateJwtToken(authentication);
    }

    @Test
    @DisplayName("Find All Teachers → Returns All Teachers")
    public void testFindAllTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Find Teacher by ID → Returns the Teacher")
    public void testFindTeacherById_ExistingId() throws Exception {
        mockMvc.perform(get("/api/teacher/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Find Teacher by ID → Returns Null for Non-Existing ID")
    public void testFindTeacherById_NonExistingId() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", 999L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
