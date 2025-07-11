package sn.ditontineplateform.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class BaseCustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String developerMessage;
    private final HttpStatus httpStatus;
    private final Map<String, Object> additionalData;
    private final String traceId;

    protected BaseCustomException(ErrorCode errorCode, String userMessage, String developerMessage,
                                  HttpStatus httpStatus, Map<String, Object> additionalData) {
        super(userMessage != null ? userMessage : errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.developerMessage = developerMessage;
        this.httpStatus = httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR;
        this.additionalData = additionalData != null ? additionalData : new HashMap<>();
        this.traceId = UUID.randomUUID().toString().substring(0, 8);
    }

    protected BaseCustomException(ErrorCode errorCode, String userMessage, String developerMessage, HttpStatus httpStatus) {
        this(errorCode, userMessage, developerMessage, httpStatus, null);
    }

    protected BaseCustomException(ErrorCode errorCode, HttpStatus httpStatus) {
        this(errorCode, null, null, httpStatus, null);
    }
}