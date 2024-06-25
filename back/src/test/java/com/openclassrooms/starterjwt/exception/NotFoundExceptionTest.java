package com.openclassrooms.starterjwt.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NotFoundExceptionTest {

    @Test
    @DisplayName("NotFoundException should have HttpStatus.NOT_FOUND")
    public void testNotFoundExceptionHttpStatus() {
        // Act
        NotFoundException exception = new NotFoundException();

        // Assert
        ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
        assertEquals(HttpStatus.NOT_FOUND, responseStatus.value());
    }

    ;

    @Test
    @DisplayName("NotFoundException should be thrown")
    public void testNotFoundExceptionThrown() {
        // Act & Assert
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException();
        });
        assertEquals(thrown.getClass(), NotFoundException.class);
    }
;
}
