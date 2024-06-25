package com.openclassrooms.starterjwt.mapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;

public class TeacherMapperTest {

    private TeacherMapper teacherMapper;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    public void setUp() {
        teacherMapper = Mappers.getMapper(TeacherMapper.class);

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
    public void testToDto() {
        TeacherDto dto = teacherMapper.toDto(teacher);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(teacher.getId());
        assertThat(dto.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(dto.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(dto.getCreatedAt()).isEqualTo(teacher.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(teacher.getUpdatedAt());
    }

    @Test
    public void testToEntity() {
        Teacher entity = teacherMapper.toEntity(teacherDto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(teacherDto.getId());
        assertThat(entity.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(entity.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(entity.getCreatedAt()).isEqualTo(teacherDto.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(teacherDto.getUpdatedAt());
    }

    @Test
    void testToDto_WithNullEntity() {
        // Act
        TeacherDto result = teacherMapper.toDto((Teacher) null);

        // Assert
        assertNull(result);
    }

    @Test
    void testToEntity_WithNullDto() {
        // Act
        Teacher result = teacherMapper.toEntity((TeacherDto) null);

        // Assert
        assertNull(result);
    }

    @Test
    public void testToDtoList() {
        List<Teacher> teachers = Collections.singletonList(teacher);
        List<TeacherDto> dtos = teacherMapper.toDto(teachers);

        assertThat(dtos).isNotNull();
        assertThat(dtos.size()).isEqualTo(1);
        assertThat(dtos.get(0)).isEqualTo(teacherMapper.toDto(teacher));
    }

    @Test
    public void testToEntityList() {
        List<TeacherDto> dtos = Collections.singletonList(teacherDto);
        List<Teacher> entities = teacherMapper.toEntity(dtos);

        assertThat(entities).isNotNull();
        assertThat(entities.size()).isEqualTo(1);
        assertThat(entities.get(0)).isEqualTo(teacherMapper.toEntity(teacherDto));
    }

    @Test
    public void testToEntity_EmptyList() {
        // Act
        List<Teacher> result = teacherMapper.toEntity(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    ;

    @Test
    public void testToDto_EmptyList() {
        // Act
        List<TeacherDto> result = teacherMapper.toDto(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToEntity_nullList() {
        // Act
        List<TeacherDto> list = null;
        List<Teacher> result = teacherMapper.toEntity(list);

        // Assert
        assertNull(result);
    }

    ;

    @Test
    public void testToDto_nullList() {
        // Act
        List<Teacher> list = null;
        List<TeacherDto> result = teacherMapper.toDto(list);

        // Assert
        assertNull(result);
    }
}
