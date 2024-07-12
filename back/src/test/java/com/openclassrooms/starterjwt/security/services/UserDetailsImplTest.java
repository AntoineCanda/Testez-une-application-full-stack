package com.openclassrooms.starterjwt.security.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.User;

@ExtendWith(MockitoExtension.class)
public class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("Jo")
                .firstName("John")
                .lastName("Doe")
                .admin(true)
                .password("password")
                .build();
    }

    @Test
    @DisplayName("userDetails -> success")
    public void testUserDetails_Success() {
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("Jo");
        assertThat(userDetails.getFirstName()).isEqualTo("John");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertTrue(userDetails.getAdmin());
    }

    @Test
    @DisplayName("userDetailsAccount -> success")
    public void testUserDetailsAccount_Success() {
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("userDetailsAuthorities -> success")
    public void testUserDetailsAuthorities_Success() {
        assertThat(userDetails.getAuthorities()).isNotNull();
        assertThat(userDetails.getAuthorities()).isEmpty();;
    }

    @Test
    @DisplayName("userDetailsEquals with creation of same user -> success")
    public void testUserDetailsEquals_sameUserCreation_Success() {
        UserDetailsImpl sameUser = UserDetailsImpl.builder()
                .id(1L)
                .username("Jo")
                .firstName("John")
                .lastName("Doe")
                .admin(true)
                .password("password")
                .build();

        assertTrue(sameUser.equals(userDetails));
    }

    @Test
    @DisplayName("userDetailsEquals -> success")
    public void testUserDetailsEquals_SameUser_Success() {
        assertTrue(userDetails.equals(userDetails));
    }

    @Test
    @DisplayName("userDetailsEquals -> false")
    public void testUserDetailsEquals_false() {
        UserDetailsImpl differentUser = UserDetailsImpl.builder()
                .id(2L)
                .username("Bobby")
                .firstName("Bob")
                .lastName("Marley")
                .admin(false)
                .password("password")
                .build();

        assertThat(differentUser).isNotEqualTo(userDetails);
    }

    @Test
    @DisplayName("userDetailsEquals with other class -> false")
    public void testUserDetailsEquals_ClassDifferent_false() {
        User user = new User();

        assertFalse(userDetails.equals(user));
    }

    @Test
    @DisplayName("userDetailsEquals with null user-> false")
    public void testUserDetailsEquals_nullUser_false() {
        UserDetailsImpl other = (UserDetailsImpl) null;

        assertFalse(userDetails.equals(other));
    }
}
