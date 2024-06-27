package com.example.card.advices;

import com.example.card.error.CardNotFoundError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CardAdvice {
    private final ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler({CardNotFoundError.class})
    @ApiResponse(responseCode = "404",
            description = "Card not found",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    public ResponseEntity<Map<String, String>> modelNotFound(CardNotFoundError error) {
        Map<String, String> map = new HashMap<>();
        map.put("message", error.getMessage());
        log.error("Error card not found, message: " + error.getMessage());
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Invalid card data")
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Invalid card with field errors (spring web error): " + mapper.writeValueAsString(errors));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ApiResponse(responseCode = "400", description = "Invalid card data")
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            ConstraintViolationException ex) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach((error) -> {
            String property = error.getPropertyPath().toString();
            int lastDot = property.lastIndexOf('.');

            String fieldName = property.substring(lastDot + 1);
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Invalid card with field errors (jakarta error): " + mapper.writeValueAsString(errors));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Map<String, String>> handlePropertyException(PropertyReferenceException ex)
    {
        Map<String, String> map = new HashMap<>();
        map.put("message", ex.getMessage());
        log.warn(ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ApiResponse(responseCode = "400", description = "Invalid card data")
    public ResponseEntity<Map<String, String>> integrityViolation(SQLIntegrityConstraintViolationException error) {
        Map<String, String> map = new HashMap<>();
        map.put("message", error.getMessage());

        log.error("Sql integrity violation: " + error.getMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
