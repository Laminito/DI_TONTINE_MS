package sn.ditontineplateform.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sn.ditontineplateform.response.CustomResponse;
import sn.ditontineplateform.utils.Constants;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<CustomResponse> handleBaseCustomException (BaseCustomException ex) {
        log.warn(
                "Custom exception [{}]: {} | Developer: {} | TraceId: {}",
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                ex.getDeveloperMessage(),
                ex.getTraceId()
        );

        CustomResponse response =
                CustomResponse.builder()
                        .code(ex.getErrorCode().getCode())
                        .statusCodeValue(ex.getHttpStatus().value())
                        .message(ex.getMessage())
                        .developerMessage(ex.getDeveloperMessage())
                        .timestamp(LocalDateTime.now())
                        .traceId(ex.getTraceId())
                        .data(null)
                        .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CustomResponse> handleValidationException (ValidationException ex) {
        log.warn(
                "Validation exception [{}]: {} | TraceId: {}",
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                ex.getTraceId()
        );

        CustomResponse response =
                CustomResponse.builder()
                        .code(ex.getErrorCode().getCode())
                        .statusCodeValue(ex.getHttpStatus().value())
                        .message(ex.getMessage())
                        .developerMessage(ex.getDeveloperMessage())
                        .timestamp(LocalDateTime.now())
                        .traceId(ex.getTraceId())
                        .validationErrors(ex.getValidationErrors())
                        .data(null)
                        .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse> handleMethodArgumentNotValid (
            MethodArgumentNotValidException ex
    ) {
        List<CustomResponse.ValidationError> validationErrors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                error -> {
                                    error.getRejectedValue();
                                    return new CustomResponse.ValidationError(
                                            error.getField(),
                                            error.getRejectedValue().toString(),
                                            error.getDefaultMessage()
                                    );
                                })
                        .collect(Collectors.toList());

        String traceId = UUID.randomUUID().toString().substring(0, 8);
        log.warn(
                "Validation error | TraceId: {} | Fields: {}",
                traceId,
                validationErrors.stream()
                        .map(CustomResponse.ValidationError::getField)
                        .collect(Collectors.joining(", "))
        );

        CustomResponse response =
                CustomResponse.builder()
                        .code(ErrorCode.VALIDATION_ERROR.getCode())
                        .statusCodeValue(HttpStatus.BAD_REQUEST.value())
                        .message("Données de saisie invalides")
                        .developerMessage(
                                MessageFormat.format(
                                        "Request validation failed for {0} field(s)", validationErrors.size()))
                        .timestamp(LocalDateTime.now())
                        .traceId(traceId)
                        .validationErrors(validationErrors)
                        .data(null)
                        .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> handleGenericException (Exception ex) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        log.error(
                "Unhandled exception | TraceId: {} | Exception: {}",
                traceId,
                ex.getClass().getSimpleName(),
                ex
        );

        CustomResponse response =
                CustomResponse.builder()
                        .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                        .statusCodeValue(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Une erreur interne s'est produite")
                        .developerMessage(
                                MessageFormat.format(
                                        "Unexpected error: {0} - {1}", ex.getClass().getSimpleName(), ex.getMessage()))
                        .timestamp(LocalDateTime.now())
                        .traceId(traceId)
                        .data(null)
                        .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private HttpStatus mapToStatus (String codeMessage) {
        return switch (codeMessage) {
            case Constants.Message.VALIDATION_ERROR_BODY -> HttpStatus.BAD_REQUEST;
            case Constants.Message.UNAUTHORIZED_BODY -> HttpStatus.UNAUTHORIZED;
            case Constants.Message.FORBIDDEN_BODY -> HttpStatus.FORBIDDEN;
            case Constants.Message.LOCKED_BODY -> HttpStatus.LOCKED;
            case Constants.Message.CONFLICT_BODY -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
