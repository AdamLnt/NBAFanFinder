package com.NBAFanFinder.Backend.Exceptions;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.NBAFanFinder.Backend.DTOs.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400 - Argument métier invalide (ex: champ vide, utilisateur non trouvé)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    // 400 - Paramètre de requête manquant (ex: ?userId absent)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Paramètre manquant : " + e.getParameterName()));
    }

    // 400 - Mauvais type de paramètre (ex: ?userId=abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Valeur invalide pour le paramètre : " + e.getName()));
    }

    // 400 - JSON malformé ou body manquant
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Corps de la requête invalide ou manquant"));
    }

    // 404 - Route inexistante
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse("Route introuvable"));
    }

    // 401 - Non autorisé (ex: mauvais mot de passe)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(401).body(new ErrorResponse(e.getMessage()));
    }

    // 404 - Ressource non trouvée
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundEntity(NotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
    }

    // 409 - Violation de contrainte DB (ex: email déjà utilisé)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException e) {
        log.warn("DataIntegrityViolationException", e);
        return ResponseEntity.status(409).body(new ErrorResponse("Conflit : une ressource avec ces données existe déjà"));
    }

    // 500 - Toute autre erreur non gérée : on log la cause cote serveur, on ne fuite rien au client
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        String requestId = UUID.randomUUID().toString();
        log.error("Unhandled exception [requestId={}]", requestId, e);
        return ResponseEntity.status(500).body(new ErrorResponse(
            "Erreur interne. Référence : " + requestId
        ));
    }
}
