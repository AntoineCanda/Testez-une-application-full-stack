package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.jwt.AuthTokenFilter;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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

    ;

   @Test
    public void testToEntity() {
        // Act
        Session sessionMapped = sessionMapper.toEntity(sessionDto);

        // Assert
        assertEquals(session.getId(), sessionMapped.getId());
        assertThat(session.getUsers().get(0).getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testToEntity_NullTeacherId() {
        // Arrange
        sessionDto.setTeacher_id(null);

        // Act
        Session result = sessionMapper.toEntity(sessionDto);

        // Assert
        assertNotNull(result);
        assertNull(result.getTeacher());
    }

    ;

    @Test
    public void testToEntity_NullUserList() {
        // Arrange
        sessionDto.setUsers(null);

        // Act
        Session result = sessionMapper.toEntity(sessionDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsers().isEmpty());
    }

    @Test
    public void testNullEntity_toDto() {
        // Arrange
        // Act
        SessionDto result = sessionMapper.toDto((Session) null);

        // Assert
        assertNull(result);
    }

    @Test
    public void testNullListEntity_toDto() {
        // Arrange
        // Act
        List<SessionDto> result = sessionMapper.toDto((List<Session>) null);

        // Assert
        assertNull(result);
    }

    @Test
    public void testToDto() {
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
    public void testNullListDto_toEntity() {
        // Arrange
        // Act
        List<Session> result = sessionMapper.toEntity((List<SessionDto>) null);

        // Assert
        assertNull(result);
    }

    @Test
    public void testNullDto_toEntity() {
        // Arrange
        // Act
        Session result = sessionMapper.toEntity((SessionDto) null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("toDto should handle null Teacher correctly")
    public void testToDto_NullTeacher() {
        // Arrange
        session.setTeacher(null);

        // Act
        SessionDto result = sessionMapper.toDto(session);

        // Assert
        assertNotNull(result);
        assertNull(result.getTeacher_id());
    }

    ;

    @Test
    @DisplayName("toDto should handle null User list correctly")
    public void testToDto_NullUserList() {
        // Arrange
        session.setUsers(null);

        // Act
        SessionDto result = sessionMapper.toDto(session);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsers().isEmpty());
    }

    ;

    @Test
    public void testToEntityList() {
        // Arrange
        List<SessionDto> sessionDtos = Collections.singletonList(sessionDto);

        // Act
        List<Session> result = sessionMapper.toEntity(sessionDtos);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sessionDto.getDescription(), result.get(0).getDescription());
    }

    ;

    @Test
    public void testToDtoList() {
        // Arrange
        List<Session> sessions = Collections.singletonList(session);

        // Act
        List<SessionDto> result = sessionMapper.toDto(sessions);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(session.getDescription(), result.get(0).getDescription());
    }

    ;

    @Test
    public void testToEntity_EmptyList() {
        // Act
        List<Session> result = sessionMapper.toEntity(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    ;

    @Test
    public void testToDto_EmptyList() {
        // Act
        List<SessionDto> result = sessionMapper.toDto(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSessionTeacherId_sucess() throws IllegalArgumentException, NoSuchMethodException, SecurityException {
        try {
            assertThat(this.getSessionTeacherIdMethod().invoke(this.sessionMapper, session)).isEqualTo(1L);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSessionTeacherId_teacherNull() throws IllegalArgumentException, NoSuchMethodException, SecurityException {
        session.setTeacher(null);

        try {
            assertThat(this.getSessionTeacherIdMethod().invoke(this.sessionMapper, session)).isNull();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
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
