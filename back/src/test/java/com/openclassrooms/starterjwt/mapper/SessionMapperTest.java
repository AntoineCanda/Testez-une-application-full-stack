package com.openclassrooms.starterjwt.mapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SessionMapperTest {

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private UserService userService;

    @InjectMocks
    private SessionMapperImpl sessionMapper;

    private SessionDto sessionDto;
    private Session session;
    private Teacher teacher;
    private User user;

    @BeforeEach
    public void setup() throws ParseException {
        sessionDto = new SessionDto(
                1L,
                "sessionName",
                new Date(),
                1L,
                "description",
                List.of(1L),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        teacher = Teacher.builder()
                .id(1L)
                .lastName("lastName")
                .firstName("firstname")
                .build();
        user = User.builder()
                .id(1L)
                .email("test@email.fr")
                .firstName("firstname")
                .lastName("lastname")
                .password("password")
                .build();
        session = Session.builder()
                .id(1L)
                .name("sessionName")
                .date(new Date())
                .teacher(teacher)
                .users(List.of(user))
                .description("description")
                .build();

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user);
    }

    @Test
    @DisplayName("Dto to Entity → Success")
    public void testToEntity_success() {
        // Act
        Session sessionMapped = sessionMapper.toEntity(sessionDto);

        // Assert
        assertEquals(session.getId(), sessionMapped.getId());
        assertThat(session.getUsers().get(0).getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Dto to Entity with null teacher id → Success with null teacher")
    public void testToEntity_NullTeacherId_nullTeacherId() {
        // Arrange
        sessionDto.setTeacher_id(null);

        // Act
        Session result = sessionMapper.toEntity(sessionDto);

        // Assert
        assertNotNull(result);
        assertNull(result.getTeacher());
    }

    @Test
    @DisplayName("Dto to Entity with null user list → Success with empty user list")
    public void testToEntity_NullUserList_emptyList() {
        // Arrange
        sessionDto.setUsers(null);

        // Act
        Session result = sessionMapper.toEntity(sessionDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsers().isEmpty());
    }

    @Test
    @DisplayName("Entity to Dto with null session → null")
    public void testNullEntity_toDto_null() {
        // Arrange
        // Act
        SessionDto result = sessionMapper.toDto((Session) null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Entity list to Dto with null list → null")
    public void testNullListEntity_toDto_null() {
        // Arrange
        // Act
        List<SessionDto> result = sessionMapper.toDto((List<Session>) null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Entity to Dto → success")
    public void testToDto_success() {
        // Act
        SessionDto result = sessionMapper.toDto(session);

        // Assert
        assertNotNull(result);
        assertEquals(session.getDescription(), result.getDescription());
        assertEquals(session.getTeacher().getId(), result.getTeacher_id());
        assertEquals(1, result.getUsers().size());
        assertEquals(user.getId(), result.getUsers().get(0));
    }

    @Test
    @DisplayName("Null list of dto to entity → null")
    public void testNullListDto_toEntity_null() {
        // Arrange
        // Act
        List<Session> result = sessionMapper.toEntity((List<SessionDto>) null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Null dto to entity → null")
    public void testNullDto_toEntity_null() {
        // Arrange
        // Act
        Session result = sessionMapper.toEntity((SessionDto) null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("To dto with null teacher -> null teacher")
    public void testToDto_NullTeacher_nullTeacher() {
        // Arrange
        session.setTeacher(null);

        // Act
        SessionDto result = sessionMapper.toDto(session);

        // Assert
        assertNotNull(result);
        assertNull(result.getTeacher_id());
    }

    @Test
    @DisplayName("To dto with null list of users -> empty list of user")
    public void testToDto_NullUserList_emptyList() {
        // Arrange
        session.setUsers(null);

        // Act
        SessionDto result = sessionMapper.toDto(session);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsers().isEmpty());
    }

    @Test
    @DisplayName("List of dto to entity -> success")
    public void testToEntityList_success() {
        // Arrange
        List<SessionDto> sessionDtos = Collections.singletonList(sessionDto);

        // Act
        List<Session> result = sessionMapper.toEntity(sessionDtos);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sessionDto.getDescription(), result.get(0).getDescription());
    }

    @Test
    @DisplayName("To dto with list of sessions -> success")
    public void testToDtoList_success() {
        // Arrange
        List<Session> sessions = Collections.singletonList(session);

        // Act
        List<SessionDto> result = sessionMapper.toDto(sessions);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(session.getDescription(), result.get(0).getDescription());
    }

    @Test
    @DisplayName("To entity with empty list of sessions -> Empty list")
    public void testToEntity_EmptyList_emptyList() {
        // Act
        List<Session> result = sessionMapper.toEntity(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("To dto with empty list of sessions -> empty list")
    public void testToDto_EmptyList_emptyList() {
        // Act
        List<SessionDto> result = sessionMapper.toDto(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("test private method of class sessionTeacherId -> success")
    public void testSessionTeacherId_sucess() throws IllegalArgumentException, NoSuchMethodException, SecurityException {
        try {
            assertThat(this.getSessionTeacherIdMethod().invoke(this.sessionMapper, session)).isEqualTo(1L);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("test private method of class sessionTeacherId -> null teacher")
    public void testSessionTeacherId_teacherNull() throws IllegalArgumentException, NoSuchMethodException, SecurityException {
        session.setTeacher(null);

        try {
            assertThat(this.getSessionTeacherIdMethod().invoke(this.sessionMapper, session)).isNull();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("test private method of class sessionTeacherId -> null teacher id")
    public void testSessionTeacherId_teacherNullId() throws IllegalArgumentException, NoSuchMethodException, SecurityException {
        teacher.setId(null);
        session.setTeacher(teacher);

        try {
            assertThat(this.getSessionTeacherIdMethod().invoke(this.sessionMapper, session)).isNull();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("test private method of class sessionTeacherId -> null session")
    public void testSessionTeacherId_sessionNull() throws IllegalArgumentException, NoSuchMethodException, SecurityException {
        session = null;

        try {
            assertThat(this.getSessionTeacherIdMethod().invoke(this.sessionMapper, session)).isNull();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Method getSessionTeacherIdMethod() throws NoSuchMethodException, SecurityException {
        Method method = SessionMapperImpl.class.getDeclaredMethod("sessionTeacherId", Session.class);
        method.setAccessible(true);
        return method;
    }
}
