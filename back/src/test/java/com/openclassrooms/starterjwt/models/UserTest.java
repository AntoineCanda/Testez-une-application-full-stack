package com.openclassrooms.starterjwt.models;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {

    private User user;
    private User sameUser;
    private User differentUser;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setLastName("Doe");
        user.setFirstName("John");
        user.setAdmin(true);
        user.setPassword("password");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        sameUser = new User();
        sameUser.setId(1L);
        sameUser.setEmail("test@example.com");
        sameUser.setLastName("Doe");
        sameUser.setFirstName("John");
        sameUser.setAdmin(true);
        sameUser.setPassword("password");
        sameUser.setCreatedAt(LocalDateTime.now());
        sameUser.setUpdatedAt(LocalDateTime.now());

        differentUser = new User();
        differentUser.setId(2L);
        differentUser.setEmail("different@example.com");
        differentUser.setLastName("Smith");
        differentUser.setFirstName("Jane");
        differentUser.setAdmin(false);
        differentUser.setPassword("different_password");
        differentUser.setCreatedAt(LocalDateTime.now());
        differentUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testGettersAndSetters() {
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.isAdmin()).isTrue();
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testEqualsAndHashCode() {

        assertThat(user).isEqualTo(sameUser);
        assertThat(user.hashCode()).isEqualTo(sameUser.hashCode());

        assertNotEquals(user, differentUser);
        assertNotEquals(user.hashCode(), differentUser.hashCode());
    }

    @Test
    public void testEqualsWithDifferentObject() {
        assertNotEquals(user, new Object());
    }

    @Test
    public void testEqualsWithNull() {
        assertNotEquals(user, null);
    }
}
