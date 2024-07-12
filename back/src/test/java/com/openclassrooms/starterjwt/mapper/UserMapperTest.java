package com.openclassrooms.starterjwt.mapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for UserMapper")
public class UserMapperTest {

    private UserMapper userMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userDto = new UserDto(
                1L,
                "test@example.com",
                "Doe",
                "John",
                false,
                "password",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    ;

    @Test
    @DisplayName("dto to entity -> success")
    public void testToEntity_success() {
        // Act
        User entity = userMapper.toEntity(userDto);

        // Assert
        assertNotNull(entity);
        assertEquals(userDto.getId(), entity.getId());
        assertEquals(userDto.getEmail(), entity.getEmail());
        assertEquals(userDto.getLastName(), entity.getLastName());
        assertEquals(userDto.getFirstName(), entity.getFirstName());
        assertEquals(userDto.getPassword(), entity.getPassword());
        assertEquals(userDto.isAdmin(), entity.isAdmin());
        assertEquals(userDto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(userDto.getUpdatedAt(), entity.getUpdatedAt());
    }

    ;

    @Test
    @DisplayName("entity to Dto -> success")
    public void testToDto_success() {
        // Act
        UserDto dto = userMapper.toDto(user);

        // Assert
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getLastName(), dto.getLastName());
        assertEquals(user.getFirstName(), dto.getFirstName());
        assertEquals(user.getPassword(), dto.getPassword());
        assertEquals(user.isAdmin(), dto.isAdmin());
        assertEquals(user.getCreatedAt(), dto.getCreatedAt());
        assertEquals(user.getUpdatedAt(), dto.getUpdatedAt());
    }

    ;

    @Test
    @DisplayName("dto list to entity -> success")
    public void testToEntityList_success() {
        // Arrange
        List<UserDto> userDtoList = Collections.singletonList(userDto);

        // Act
        List<User> users = userMapper.toEntity(userDtoList);

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(userDto.getId(), users.get(0).getId());
        assertEquals(userDto.getEmail(), users.get(0).getEmail());
        assertEquals(userDto.getLastName(), users.get(0).getLastName());
        assertEquals(userDto.getFirstName(), users.get(0).getFirstName());
        assertEquals(userDto.getPassword(), users.get(0).getPassword());
        assertEquals(userDto.isAdmin(), users.get(0).isAdmin());
        assertEquals(userDto.getCreatedAt(), users.get(0).getCreatedAt());
        assertEquals(userDto.getUpdatedAt(), users.get(0).getUpdatedAt());
    }

    ;

    @Test
    @DisplayName("entity list to dto -> success")
    public void testToDtoList_success() {
        // Arrange
        List<User> usersList = Collections.singletonList(user);

        // Act
        List<UserDto> usersDto = userMapper.toDto(usersList);

        // Assert
        assertNotNull(usersDto);
        assertEquals(1, usersDto.size());
        assertEquals(user.getId(), usersDto.get(0).getId());
        assertEquals(user.getLastName(), usersDto.get(0).getLastName());
        assertEquals(user.getFirstName(), usersDto.get(0).getFirstName());
        assertEquals(user.getCreatedAt(), usersDto.get(0).getCreatedAt());
        assertEquals(user.getUpdatedAt(), usersDto.get(0).getUpdatedAt());
    }

    @Test
    @DisplayName("null entity list to dto -> null")
    void testToDtoList_WithNullEntityList_null() {
        // act
        List<UserDto> dtoList = userMapper.toDto((List<User>) null);

        // assert
        assertNull(dtoList);
    }

    @Test
    @DisplayName("null dto list to entity -> null")
    void testToEntity_WithNullDtoList_null() {
        // act
        List<User> entityList = userMapper.toEntity((List<UserDto>) null);

        // assert
        assertNull(entityList);
    }

    @Test
    @DisplayName("null dto to entity -> null")
    void testToEntity_WithNullDto_null() {

        // act
        User result = userMapper.toEntity((UserDto) null);

        // assert
        assertNull(result);
    }

    @Test
    @DisplayName("null dto to entity-> null")
    void testToDto_WithNullEntity_null() {
        // act
        UserDto result = userMapper.toDto((User) null);

        // assert
        assertNull(result);
    }
}
