package com.openclassrooms.starterjwt.controllers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    public void setUp() {
        teacher = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        teacherDto = new TeacherDto(
                1L,
                "Doe",
                "John",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Find teacher by ID → Success")
    public void testFindById_Success() {
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        ResponseEntity<?> response = teacherController.findById("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(teacherDto);

        verify(teacherService, times(1)).findById(1L);
        verify(teacherMapper, times(1)).toDto(teacher);
    }

    @Test
    @DisplayName("Find teacher by ID → User not found")
    public void testFindById_NotFound() {
        when(teacherService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = teacherController.findById("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(404);

        verify(teacherService, times(1)).findById(1L);
        verify(teacherMapper, times(0)).toDto(any(Teacher.class));
    }

    @Test
    @DisplayName("Find teacher by ID with invalid id→ Bad Request")
    public void testFindById_BadRequest() {
        ResponseEntity<?> response = teacherController.findById("invalid");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verify(teacherService, times(0)).findById(anyLong());
        verify(teacherMapper, times(0)).toDto(any(Teacher.class));
    }

    @Test
    @DisplayName("Find all teacher → Success")
    public void testFindAll_Success() {
        List<Teacher> teachers = Collections.singletonList(teacher);
        List<TeacherDto> teacherDtos = Collections.singletonList(teacherDto);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        ResponseEntity<?> response = teacherController.findAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(teacherDtos);

        verify(teacherService, times(1)).findAll();
        verify(teacherMapper, times(1)).toDto(teachers);
    }
}
