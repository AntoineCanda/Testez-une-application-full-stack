package com.openclassrooms.starterjwt.controllers.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController Integration Tests")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static String jwt;

    @BeforeEach
    public void setUp(@Autowired AuthenticationManager authenticationManager,
            @Autowired JwtUtils jwtUtils) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("yoga@studio.com", "test!1234"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        jwt = jwtUtils.generateJwtToken(authentication);
    }

    @Test
    @DisplayName("Find User by ID → Returns the User")
    public void testFindUserById_UserExists() throws Exception {
        mockMvc.perform(get("/api/user/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastName").value("Admin"));
    }

    @Test
    @DisplayName("Find User by ID → Returns Null for Non-Existing ID")
    public void testFindUserById_UserNotFound() throws Exception {
        mockMvc.perform(get("/api/user/999")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete User by ID → Returns Null for Non-Existing ID")
    public void testDeleteUserById_UserNotFound() throws Exception {
        mockMvc.perform(delete("/api/user/0")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
