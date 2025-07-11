package sn.ditontineplateform.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomRuntimeException extends RuntimeException {
  private final HttpStatus status;
  private final String errorCode;

  public CustomRuntimeException(String message) {
    super(message);
    this.status = HttpStatus.BAD_REQUEST;
    this.errorCode = "VALIDATION_ERROR";
  }

  public CustomRuntimeException(String message, HttpStatus status) {
    super(message);
    this.status = status;
    this.errorCode = "VALIDATION_ERROR";
  }

  public CustomRuntimeException(String message, HttpStatus status, String errorCode) {
    super(message);
    this.status = status;
    this.errorCode = errorCode;
  }
}
