package com.fiap.globalsolution.exception;

import com.fiap.globalsolution.service.LearningPathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        LocaleContextHolder.setLocale(Locale.US);
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnLocalizedMessage() {
        // Arrange
        String resourceId = "123";
        ResourceNotFoundException ex = new ResourceNotFoundException(resourceId);
        String expectedMessage = "Learning path with ID 123 not found";

        when(messageSource.getMessage(eq("learning.path.not.found"), eq(new Object[]{resourceId}), any(Locale.class)))
                .thenReturn(expectedMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleResourceNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().get("error"));
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnLocalizedMessage_PT() {
        // Arrange
        LocaleContextHolder.setLocale(new Locale("pt", "BR"));
        String resourceId = "456";
        ResourceNotFoundException ex = new ResourceNotFoundException(resourceId);
        String expectedMessage = "Trilha não encontrada com o ID: 456";

        when(messageSource.getMessage(eq("learning.path.not.found"), eq(new Object[]{resourceId}), any(Locale.class)))
                .thenReturn(expectedMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleResourceNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().get("error"));
    }
}
