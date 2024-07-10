package com.openclassrooms.starterjwt.security.services;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    @DisplayName("load user by username -> success")
    public void testLoadUserByUsername_Success() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("john.doe@example.fr");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setPassword("password");

        when(userRepository.findByEmail("john.doe@example.fr")).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe@example.fr");

        // Assert
        assertNotNull(userDetails);
        assertThat(userDetails.getUsername()).isEqualTo("john.doe@example.fr");
        assertThat(userDetails.getPassword()).isEqualTo("password");

        verify(userRepository, times(1)).findByEmail("john.doe@example.fr");
    }

    @Test
    @DisplayName("load user by username -> UsernameNotFoundException")
    public void testLoadUserByUsername_FailureUsernameNotFound() {
        // Arrange
        String username = "john.doe@example.fr";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });

        verify(userRepository, times(1)).findByEmail(username);
    }
}
