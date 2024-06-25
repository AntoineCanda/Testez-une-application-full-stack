package com.openclassrooms.starterjwt.security.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
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
    public void testUserDetails_Success() {
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("Jo");
        assertThat(userDetails.getFirstName()).isEqualTo("John");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertTrue(userDetails.getAdmin());
    }

    @Test
    public void testUserDetailsAccount_Success() {
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    public void testUserDetailsAuthorities_Success() {
        assertThat(userDetails.getAuthorities()).isNotNull();
        assertThat(userDetails.getAuthorities()).isEmpty();;
    }

    @Test
    public void testUserDetailsEquals_UserWithSameValueSuccess() {
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
    public void testUserDetailsEquals_SameUserSuccess() {
        assertTrue(userDetails.equals(userDetails));
    }

    @Test
    public void testUserDetailsEquals_Fail() {
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
    public void testUserDetailsEquals_ClassDifferent() {
        User user = new User();

        assertFalse(userDetails.equals(user));
    }

    @Test
    public void testUserDetailsEquals_nullUser() {
        UserDetailsImpl other = (UserDetailsImpl) null;

        assertFalse(userDetails.equals(other));
    }
}
