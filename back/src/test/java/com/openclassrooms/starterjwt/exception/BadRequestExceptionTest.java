package com.openclassrooms.starterjwt.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BadRequestExceptionTest {

    @Test
    @DisplayName("BadRequestException has good status")
    public void testBadRequestException_HttpStatus() {
        // Act
        BadRequestException exception = new BadRequestException();

        // Assert
        ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseStatus.value());
    }

    @Test
    @DisplayName("BadRequestException should be thrown")
    public void testBadRequestException_ExceptionThrown() {
        // Act & Assert
        Throwable thrown = assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException();
        });
        assertEquals(thrown.getClass(), BadRequestException.class);
    }
}
