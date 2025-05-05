package in.gppalanpur.portal.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND, request);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.UNAUTHORIZED, request);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.FORBIDDEN, request);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return buildResponseEntity(new UnauthorizedException("Invalid credentials"), HttpStatus.UNAUTHORIZED, request);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        BindingResult result = ex.getBindingResult();
        List<ApiError.FieldError> fieldErrors = result.getFieldErrors().stream()
                .map(fieldError -> new ApiError.FieldError(
                        fieldError.getField(), 
                        fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .message("Validation error")
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errors(fieldErrors)
                .build();
        
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        List<ApiError.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(violation -> new ApiError.FieldError(
                        violation.getPropertyPath().toString(), 
                        violation.getMessage()))
                .collect(Collectors.toList());
        
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .message("Validation error")
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errors(fieldErrors)
                .build();
        
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred", ex);
        return buildResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
    
    private ResponseEntity<ApiError> buildResponseEntity(Exception ex, HttpStatus status, WebRequest request) {
        ApiError apiError = ApiError.builder()
                .status(status.name())
                .message(ex.getMessage())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(apiError, status);
    }
    
    private String getPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}