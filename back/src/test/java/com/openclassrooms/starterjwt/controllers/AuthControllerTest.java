package com.openclassrooms.starterjwt.controllers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    private LoginRequest loginRequest;
    private SignupRequest signUpRequest;

    @BeforeEach
    public void setup() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password");

        signUpRequest = new SignupRequest();
        signUpRequest.setEmail("john.doe@example.com");
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");
        signUpRequest.setPassword("password");

    }

    @Test
    @DisplayName("Authenticate user → Success")
    public void testAuthenticateUser_Success() {
        // Arrange
        User user = new User("john.doe@example.com", "Doe", "John", "password", true);
        user.setId(1L);

        UserDetailsImpl userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.isAdmin(), user.getPassword());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwtToken");

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertNotNull(jwtResponse);
        assertThat(jwtResponse.getToken()).isEqualTo("jwtToken");
        assertThat(jwtResponse.getId()).isEqualTo(user.getId());
        assertThat(jwtResponse.getUsername()).isEqualTo(user.getEmail());
        assertThat(jwtResponse.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(jwtResponse.getLastName()).isEqualTo(user.getLastName());
        assertTrue(jwtResponse.getAdmin());
    }

    @Test
    @DisplayName("Authenticate user with invalid credentials → Bad credentials")
    public void testAuthenticateUser_withInvalidCredentials_badCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            authController.authenticateUser(loginRequest);
        });
        assertThat(exception).isInstanceOf(BadCredentialsException.class);
        assertThat(exception.getMessage()).isEqualTo("Invalid credentials");
        verifyNoInteractions(jwtUtils, userRepository);
    }

    @Test
    @DisplayName("Register new user → Success")
    public void testRegisterNewUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedUser.getFirstName()).isEqualTo("John");
        assertThat(savedUser.getLastName()).isEqualTo("Doe");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("Register new user with already used email → Bad Request")
    public void testRegisterWithEmailAlreadyUse_BadRequest() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertInstanceOf(MessageResponse.class, response.getBody());
    }
}
