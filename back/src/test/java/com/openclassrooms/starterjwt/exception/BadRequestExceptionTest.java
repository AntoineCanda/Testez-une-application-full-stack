package com.openclassrooms.starterjwt.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;

public class BadRequestExceptionTest {

    @Test
    @DisplayName("BadRequestException should have HttpStatus.BAD_REQUEST")
    public void testBadRequestExceptionHttpStatus() {
        // Act
        BadRequestException exception = new BadRequestException();

        // Assert
        ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseStatus.value());
    }

    ;

    @Test
    @DisplayName("BadRequestException should be thrown")
    public void testBadRequestExceptionThrown() {
        // Act & Assert
        Throwable thrown = assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException();
        });
        assertEquals(thrown.getClass(), BadRequestException.class);
    }
;
}
