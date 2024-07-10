package com.openclassrooms.starterjwt.services;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("delete user -> success")
    public void testDelete_success() {
        // Arrange
        Long id = 1L;

        // Act
        userService.delete(id);

        // Assert
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("find user by id -> success")
    public void testFindById_success() {
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
    @DisplayName("find user by id -> not found")
    public void testFindById_NotFound() {
        // Arrange
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        User foundUser = userService.findById(id);

        // Assert
        assertThat(foundUser).isNull();
    }
}
