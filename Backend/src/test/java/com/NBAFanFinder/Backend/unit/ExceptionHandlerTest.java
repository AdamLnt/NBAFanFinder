package com.NBAFanFinder.Backend.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.NBAFanFinder.Backend.DTOs.ErrorResponse;
import com.NBAFanFinder.Backend.Exceptions.GlobalExceptionHandler;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Exceptions.UnauthorizedException;

@DisplayName("Tests unitaires - GlobalExceptionHandler")
public class ExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("IllegalArgumentException -> 400")
    void shouldHandleBadRequest() {
        ResponseEntity<ErrorResponse> result = handler.handleBadRequest(new IllegalArgumentException("champ invalide"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().error()).isEqualTo("champ invalide");
    }

    @Test
    @DisplayName("MissingServletRequestParameterException -> 400 avec nom du paramètre")
    void shouldHandleMissingParam() {
        ResponseEntity<ErrorResponse> result = handler.handleMissingParam(
            new MissingServletRequestParameterException("teamId", "Long"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().error()).contains("teamId");
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchException -> 400 avec nom du paramètre")
    void shouldHandleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");

        ResponseEntity<ErrorResponse> result = handler.handleTypeMismatch(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().error()).contains("id");
    }

    @Test
    @DisplayName("HttpMessageNotReadableException -> 400")
    void shouldHandleUnreadable() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ErrorResponse> result = handler.handleUnreadable(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().error()).contains("Corps de la requête");
    }

    @Test
    @DisplayName("NoResourceFoundException -> 404")
    void shouldHandleNoResource() {
        ResponseEntity<ErrorResponse> result = handler.handleNotFound(
            mock(NoResourceFoundException.class));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().error()).isEqualTo("Route introuvable");
    }

    @Test
    @DisplayName("UnauthorizedException -> 401")
    void shouldHandleUnauthorized() {
        ResponseEntity<ErrorResponse> result = handler.handleUnauthorized(
            new UnauthorizedException("non autorisé"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody().error()).isEqualTo("non autorisé");
    }

    @Test
    @DisplayName("NotFoundException -> 404")
    void shouldHandleNotFoundEntity() {
        ResponseEntity<ErrorResponse> result = handler.handleNotFoundEntity(
            new NotFoundException("ressource absente"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().error()).isEqualTo("ressource absente");
    }

    @Test
    @DisplayName("DataIntegrityViolationException -> 409")
    void shouldHandleConflict() {
        ResponseEntity<ErrorResponse> result = handler.handleConflict(
            new DataIntegrityViolationException("duplicate"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getBody().error()).contains("Conflit");
    }

    @Test
    @DisplayName("RuntimeException non gérée -> 500 sans fuite d'information")
    void shouldHandleGenericRuntime() {
        ResponseEntity<ErrorResponse> result = handler.handleRuntimeException(
            new RuntimeException("stacktrace secrète"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody().error()).contains("Erreur interne");
        assertThat(result.getBody().error()).doesNotContain("secrète");
    }
}
