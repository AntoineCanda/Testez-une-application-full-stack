package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        session = Session.builder()
                .id(1L)
                .name("Test Session")
                .date(new Date())
                .description("Test Description")
                .teacher(Teacher.builder().id(1L).firstName("John").lastName("Doe").build())
                .users(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sessionDto = new SessionDto(
                1L,
                "Test Session",
                new Date(),
                1L,
                "Test Description",
                List.of(1L),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testFindById() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // Act
        ResponseEntity<?> response = sessionController.findById("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(sessionDto);
        verify(sessionService, times(1)).getById(1L);
        verify(sessionMapper, times(1)).toDto(session);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = sessionController.findById("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(sessionService, times(1)).getById(1L);
    }

    ;

    @Test
    void testFindById_BadRequest() {
        // Act
        ResponseEntity<?> response = sessionController.findById("invalid");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(sessionService, times(0)).getById(anyLong());
    }

    ;

    @Test
    void testFindAll_Success() {
        // Arrange
        when(sessionService.findAll()).thenReturn(Collections.singletonList(session));
        when(sessionMapper.toDto(Collections.singletonList(session))).thenReturn(Collections.singletonList(sessionDto));

        // Act
        ResponseEntity<?> response = sessionController.findAll();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(Collections.singletonList(sessionDto));
        verify(sessionService, times(1)).findAll();
        verify(sessionMapper, times(1)).toDto(Collections.singletonList(session));
    }

    @Test
    void testCreate_Success() {
        // Arrange
        when(sessionService.create(any(Session.class))).thenReturn(session);
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);

        // Act
        ResponseEntity<?> response = sessionController.create(sessionDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(sessionDto);
        verify(sessionService, times(1)).create(any(Session.class));
        verify(sessionMapper, times(1)).toEntity(any(SessionDto.class));
        verify(sessionMapper, times(1)).toDto(any(Session.class));
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        when(sessionService.update(anyLong(), any(Session.class))).thenReturn(session);
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);

        // Act
        ResponseEntity<?> response = sessionController.update("1", sessionDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(sessionDto);
        verify(sessionService, times(1)).update(anyLong(), any(Session.class));
        verify(sessionMapper, times(1)).toEntity(any(SessionDto.class));
        verify(sessionMapper, times(1)).toDto(any(Session.class));
    }

    @Test
    void testUpdate_BadRequest() {
        // Act
        ResponseEntity<?> response = sessionController.update("invalid", sessionDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(sessionService, never()).update(anyLong(), any(Session.class));
    }

    ;

    @Test
    void testSave_Success() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(session);

        // Act
        ResponseEntity<?> response = sessionController.save("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService, times(1)).getById(1L);
        verify(sessionService, times(1)).delete(1L);
    }

    @Test
    void testSave_BadRequest() {

        // Act
        ResponseEntity<?> response = sessionController.save("invalid");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(sessionService, never()).getById(anyLong());
        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    void testSave_NotFound() {
        // Arrange
        when(sessionService.getById(session.getId())).thenReturn(null);
        //Act
        ResponseEntity<?> response = sessionController.save(session.getId().toString());
        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(sessionService, times(1)).getById(1L);
        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    void testParticipate_Success() {
        // Act
        ResponseEntity<?> response = sessionController.participate("1", "1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService, times(1)).participate(1L, 1L);
    }

    @Test
    void testNoLongerParticipate() {
        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService, times(1)).noLongerParticipate(1L, 1L);
    }

    @Test
    void testNoLongerParticipate_BadRequest() {
        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate("invalid", "1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(sessionService, never()).noLongerParticipate(anyLong(), anyLong());
    }
;
}
