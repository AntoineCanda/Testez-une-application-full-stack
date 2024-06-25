package com.openclassrooms.starterjwt.models;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TeacherTest {

    private Teacher teacher;
    private Teacher sameTeacher;
    private Teacher differentTeacher;

    @BeforeEach
    public void setUp() {
        LocalDateTime now = LocalDateTime.now();
        teacher = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(now)
                .updatedAt(now)
                .build();

        sameTeacher = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(now)
                .updatedAt(now)
                .build();

        differentTeacher = Teacher.builder()
                .id(2L)
                .lastName("Smith")
                .firstName("Jane")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    public void testGettersAndSetters() {
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getLastName()).isEqualTo("Doe");
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getCreatedAt()).isNotNull();
        assertThat(teacher.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testBuilder() {
        Teacher builtTeacher = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertThat(builtTeacher.getId()).isEqualTo(1L);
        assertThat(builtTeacher.getLastName()).isEqualTo("Doe");
        assertThat(builtTeacher.getFirstName()).isEqualTo("John");
        assertThat(builtTeacher.getCreatedAt()).isNotNull();
        assertThat(builtTeacher.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testToString() {
        String toString = teacher.toString();
        assertThat(toString).contains("Teacher");
        assertThat(toString).contains("Doe");
        assertThat(toString).contains("John");
    }

    @Test
    public void testEqualsAndHashCode() {
        assertThat(teacher).isEqualTo(sameTeacher);
        assertThat(teacher.hashCode()).isEqualTo(sameTeacher.hashCode());

        assertNotEquals(teacher, differentTeacher);
        assertNotEquals(teacher.hashCode(), differentTeacher.hashCode());
    }

    @Test
    public void testEqualsWithDifferentObject() {
        assertNotEquals(teacher, new Object());
    }

    @Test
    public void testEqualsWithNull() {
        assertNotEquals(teacher, null);
    }
}
