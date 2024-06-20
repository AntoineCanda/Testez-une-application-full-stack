package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    public void testCreateSession() {
        // Arrange
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // Act
        Session savedSession = sessionService.create(new Session());

        // Assert
        verify(sessionRepository, times(1)).save(any(Session.class));
        assertEquals(savedSession, session);
    }

    @Test
    public void testDeleteSession() {
        // Act
        sessionService.delete(session.getId());

        // Assert
        verify(sessionRepository, times(1)).deleteById(session.getId());
    }

    @Test
    public void testFindAllSessions() {
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
    public void testFindAllSessions_NoSessions() {
        // Arrange
        when(sessionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Session> sessions = sessionService.findAll();

        // Assert
        verify(sessionRepository, times(1)).findAll();
        assertTrue(sessions.isEmpty());
    }

    ;

    @Test
    public void testFindByIdSessionFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session foundSession = sessionService.getById(1L);

        assertNotNull(foundSession);
        assertEquals(1L, foundSession.getId());

        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdSessionNotFound() {

        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        Session foundSession = sessionService.getById(1L);

        assertNull(foundSession);

        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateSession() {
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
    public void testParticipateInSession_SessionNotFound() {
        // Arrange
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sessionService.participate(session.getId(), user.getId()));

        verify(sessionRepository, times(1)).findById(session.getId());
    }

    @Test
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

    ;

    @Test
    public void testNoLongerParticipateInSessionNotFound() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(session.getId(), user.getId()));

        verify(sessionRepository, never()).save(any());
    }

    @Test
    public void testNoLongerParticipateInSessionNotParticipating() {
        // Arrange
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        user.setId(5L);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(session.getId(), user.getId()));

        verify(sessionRepository, times(1)).findById(session.getId());
    }
}
