package com.openclassrooms.starterjwt.security.jwt;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Test
    public void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtils.validateJwtToken("validToken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validToken")).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails);

        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(userDetailsService).loadUserByUsername("username");
        verify(userDetails).getAuthorities();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternalWithException() throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtils.validateJwtToken("validToken")).thenThrow(new RuntimeException("Simulated exception"));

        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtUtils.validateJwtToken("invalidToken")).thenReturn(false);

        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternalWithTokenWithoutBearerPrefix() throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn("invalidToken");

        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(jwtUtils, times(0)).validateJwtToken(any(String.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testParseJwt_success() throws IllegalArgumentException, NoSuchMethodException, SecurityException {
        String bearerToken = "Bearer token";
        String token = "token";
        when(request.getHeader("Authorization")).thenReturn(bearerToken);

        try {
            assertThat(this.getParseJwtMethod().invoke(this.authTokenFilter, request)).isEqualTo(token);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        verify(request, times(1)).getHeader("Authorization");
    }

    @Test
    public void testParseJwt_noBearerPrefix() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String token = "token";
        when(request.getHeader("Authorization")).thenReturn(token);

        try {
            assertThat(this.getParseJwtMethod().invoke(this.authTokenFilter, request)).isNull();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        verify(request, times(1)).getHeader("Authorization");
    }

    @Test
    public void testParseJwt_noBearerToken() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String token = null;
        when(request.getHeader("Authorization")).thenReturn(token);

        try {
            assertThat(this.getParseJwtMethod().invoke(this.authTokenFilter, request)).isNull();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        verify(request, times(1)).getHeader("Authorization");
    }

    private Method getParseJwtMethod() throws NoSuchMethodException, SecurityException {
        Method method = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        method.setAccessible(true);
        return method;
    }

}
