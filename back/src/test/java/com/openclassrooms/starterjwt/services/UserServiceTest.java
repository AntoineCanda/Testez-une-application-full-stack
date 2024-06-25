package com.openclassrooms.starterjwt.services;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDelete() {
        // Arrange
        Long id = 1L;

        // Act
        userService.delete(id);

        // Assert
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    public void testFindById() {
        // Arrange
        Long id = 1L;
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.findById(id);

        // Assert
        assertThat(foundUser).isSameAs(user);
    }

    @Test
    public void testFindByIdNotFound() {
        // Arrange
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        User foundUser = userService.findById(id);

        // Assert
        assertThat(foundUser).isNull();
    }
}
