package com.openclassrooms.starterjwt.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    private SessionService sessionService;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.fr")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .lastName("Marley")
                .firstName("Bob")
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        session = Session.builder()
                .name("Session")
                .id(1L)
                .date(Date.from(Instant.now()))
                .description("description")
                .teacher(teacher)
                .users(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        sessionService = new SessionService(sessionRepository, userRepository);
    }

    @AfterEach
    void tearDown() {
        sessionService = null;
    }

    @Test
    @DisplayName("create session -> success")
    public void testCreateSession_success() {
        // Arrange
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // Act
        Session savedSession = sessionService.create(new Session());

        // Assert
        verify(sessionRepository, times(1)).save(any(Session.class));
        assertEquals(savedSession, session);
    }

    @Test
    @DisplayName("delete session -> success")
    public void testDeleteSession_success() {
        // Act
        sessionService.delete(session.getId());

        // Assert
        verify(sessionRepository, times(1)).deleteById(session.getId());
    }

    @Test
    @DisplayName("find all sessions -> success")
    public void testFindAllSessions_success() {
        // Arrange
        when(sessionRepository.findAll()).thenReturn(List.of(session));

        // Act
        List<Session> sessions = sessionService.findAll();

        // Assert
        verify(sessionRepository, times(1)).findAll();
        assertFalse(sessions.isEmpty());
        assertEquals(sessions.size(), 1);
        assertEquals(sessions.get(0), session);
    }

    @Test
    @DisplayName("find all sessions -> empty list")
    public void testFindAllSessions_emptyList() {
        // Arrange
        when(sessionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Session> sessions = sessionService.findAll();

        // Assert
        verify(sessionRepository, times(1)).findAll();
        assertTrue(sessions.isEmpty());
    }

    @Test
    @DisplayName("find by id -> success")
    public void testFindById_success() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session foundSession = sessionService.getById(1L);

        assertNotNull(foundSession);
        assertEquals(1L, foundSession.getId());

        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("find by id -> null")
    public void testFindById_null() {

        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        Session foundSession = sessionService.getById(1L);

        assertNull(foundSession);

        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("update -> success")
    public void testUpdateSession_success() {
        // Arrange
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // Act
        Session updatedSession = sessionService.update(session.getId(), new Session());

        // Assert
        verify(sessionRepository, times(1)).save(any(Session.class));
        assertNotNull(updatedSession);
        assertEquals(updatedSession.getId(), session.getId());
    }

    @Test
    @DisplayName("participate in session -> success")
    public void testParticipateInSession_Success() {
        // Arrange
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        sessionService.participate(session.getId(), user.getId());

        // Assert
        verify(sessionRepository, times(1)).save(any(Session.class));
        assertTrue(session.getUsers().contains(user));
    }

    @Test
    @DisplayName("participate in session -> session not found")
    public void testParticipateInSession_sessionNotFound() {
        // Arrange
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sessionService.participate(session.getId(), user.getId()));

        verify(sessionRepository, times(1)).findById(session.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("participate in session -> user not found")
    public void testParticipateInSession_UserNotFound() {
        // Arrange
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sessionService.participate(session.getId(), user.getId()));

        verify(sessionRepository, times(1)).findById(session.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("participate in session with user already in the session -> bad request")
    public void testParticipateInSession_UserAlreadyParticipate_badRequest() {
        // Arrange
        session.getUsers().add(user);

        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> sessionService.participate(session.getId(), user.getId()));

        verify(sessionRepository, times(1)).findById(session.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("no longer participate in session -> success")
    public void testNoLongerParticipate_Success() {
        // Arrange
        session.getUsers().add(user);
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // Act
        sessionService.noLongerParticipate(session.getId(), user.getId());

        // Assert
        verify(sessionRepository, times(1)).findById(session.getId());
        verify(sessionRepository, times(1)).save(session);
        assertFalse(session.getUsers().contains(user));
    }

    @Test
    @DisplayName("no longer participate in session -> session not found")
    public void testNoLongerParticipateInSession_sessionNotFound() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(session.getId(), user.getId()));

        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("no longer participate in session with user not in -> bad request")
    public void testNoLongerParticipateInSession_WithUserNotParticipating_badRequest() {
        // Arrange
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        user.setId(5L);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(session.getId(), user.getId()));

        verify(sessionRepository, times(1)).findById(session.getId());
    }
}
