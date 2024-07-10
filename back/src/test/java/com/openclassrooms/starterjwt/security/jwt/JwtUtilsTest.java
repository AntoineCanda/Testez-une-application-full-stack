package com.openclassrooms.starterjwt.security.jwt;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private final String jwtSecret = "openclassrooms";
    private final int expirationTime = 3600000;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", expirationTime); // 1 hour

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("JohnDoe")
                .password("password")
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    @DisplayName("generateJwtToken with valid auth -> success")
    void testGenerateJwtToken_validAuth_success() {

        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(token).isNotNull();
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();

    }

    @Test
    @DisplayName("getUserNameFromJwtToken with valid token -> success")
    void testGetUserNameFromJwtToken_success() {

        String token = jwtUtils.generateJwtToken(authentication);

        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertThat(username).isEqualTo("JohnDoe");
    }

    @Test
    @DisplayName("validateJwtToken with valid token -> success")
    void testValidateJwtToken_success() {

        String token = jwtUtils.generateJwtToken(authentication);

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("validateJwtToken with invalid token -> success")
    void testValidateJwtToken_invalidToken_false() {
        String token = "invalidToken";

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken with invalidSignature -> false")
    void testValidateJwtToken_InvalidSignature() {
        String token = Jwts.builder()
                .setSubject("JohnDoe")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, "wrongSecret")
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken with expired token -> false")
    void testValidateJwtToken_Expired_false() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 1);
        String token = jwtUtils.generateJwtToken(authentication);

        Thread.sleep(2);

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken with unsigned token -> false")
    void validateJwtToken_UnsignedToken_false() {
        String unsupportedToken = Jwts.builder()
                .setSubject("JohnDoe")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .compact();
        boolean isValid = jwtUtils.validateJwtToken(unsupportedToken);

        assertThat(isValid).isFalse();

    }

}
