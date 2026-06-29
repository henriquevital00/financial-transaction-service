package org.example.infrastructure.adapter.in.web.exception;


import lombok.extern.slf4j.Slf4j;
import org.example.domain.exception.*;
import org.example.generated.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyExists(
        AccountAlreadyExistsException ex
    ) {
        log.warn("Account already exists: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setCode("409");
        error.setMessage("Account already exists");
        error.setLevel(ErrorResponse.LevelEnum.ERROR);
        error.setDescription(ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(
        AccountNotFoundException ex
    ) {
        log.warn("Account not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setCode("404");
        error.setMessage("Account not found");
        error.setLevel(ErrorResponse.LevelEnum.WARNING);
        error.setDescription(ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
        BusinessException ex
    ) {
        log.warn("Business error: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setCode("400");
        error.setMessage("Business rule violation");
        error.setLevel(ErrorResponse.LevelEnum.WARNING);
        error.setDescription(ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex
    ) {
        log.warn("Validation error: {}", ex.getMessage());

        String message = ex.getBindingResult()
            .getAllErrors()
            .stream()
            .map(error -> error.getDefaultMessage())
            .findFirst()
            .orElse("Invalid request parameters");

        ErrorResponse error = new ErrorResponse();
        error.setCode("400");
        error.setMessage("Validation failed");
        error.setLevel(ErrorResponse.LevelEnum.WARNING);
        error.setDescription(message);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(
        org.springframework.web.bind.MissingRequestHeaderException ex
    ) {
        log.warn("Missing request header: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setCode("400");
        error.setMessage("Missing required header");
        error.setLevel(ErrorResponse.LevelEnum.WARNING);
        error.setDescription(ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex
    ) {
        log.warn("Type mismatch error: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setCode("400");
        error.setMessage("Invalid parameter type");
        error.setLevel(ErrorResponse.LevelEnum.WARNING);
        error.setDescription(String.format("Parameter '%s' should be of type %s", ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown"));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
        org.springframework.http.converter.HttpMessageNotReadableException ex
    ) {
        log.warn("Malformed JSON request: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setCode("400");
        error.setMessage("Malformed JSON request");
        error.setLevel(ErrorResponse.LevelEnum.WARNING);
        error.setDescription("The request body is invalid or not readable.");

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    @ExceptionHandler(TransactionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleTransactionAlreadyExistsException(
        TransactionAlreadyExistsException ex
    ) {
        log.warn("Transaction already exists: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setCode("409");
        error.setMessage("Transaction already exists");
        error.setLevel(ErrorResponse.LevelEnum.ERROR);
        error.setDescription(ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
    }

    @ExceptionHandler(InvalidOperationTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationTypeException(
        InvalidOperationTypeException ex
    ) {
        log.warn("Invalid operation type: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setCode("400");
        error.setMessage("Invalid Operation Type");
        error.setLevel(ErrorResponse.LevelEnum.WARNING);
        error.setDescription(ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);

        ErrorResponse error = new ErrorResponse();
        error.setCode("500");
        error.setMessage("Internal server error");
        error.setLevel(ErrorResponse.LevelEnum.ERROR);
        error.setDescription("An unexpected error occurred. Please contact support.");

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
}

