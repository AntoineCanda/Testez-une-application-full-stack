package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.UserService;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById() {
        // Arrange
        Long id = 1L;
        User user = new User();
        user.setId(id);
        when(userService.findById(id)).thenReturn(user);

        // Act
        ResponseEntity<?> response = userController.findById(id.toString());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userMapper).toDto(user);
    }

    @Test
    public void testFindByIdNotFound() {
        // Arrange
        Long id = 1L;
        when(userService.findById(id)).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.findById(id.toString());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testFindByIdNumberFormatException() {
        // Act
        ResponseEntity<?> response = userController.findById("invalid_id");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testDelete() {
        // Arrange
        Long id = 1L;
        User user = new User();
        user.setId(id);
        when(userService.findById(id)).thenReturn(user);

        // Mock the SecurityContextHolder
        UserDetails userDetails = UserDetailsImpl.builder().username(user.getEmail()).build();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        // Act
        ResponseEntity<?> response = userController.save(id.toString());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService, times(1)).findById(1L);
        verify(userService).delete(id);
    }

    @Test
    public void testDeleteNotFound() {
        // Arrange
        Long id = 1L;
        when(userService.findById(id)).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.save(id.toString());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteUnauthorized() {
        // Arrange
        Long id = 1L;
        User user = new User();
        user.setId(id);
        when(userService.findById(id)).thenReturn(user);

        UserDetails userDetails = UserDetailsImpl.builder().username("unauthorized@example.com").build();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<?> response = userController.save(id.toString());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(userService, never()).delete(anyLong());
    }

    @Test
    public void testDeleteNumberFormatException() {
        // Act
        ResponseEntity<?> response = userController.save("invalid_id");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
