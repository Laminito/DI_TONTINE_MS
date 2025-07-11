package sn.ditontineplateform.exception;

import org.springframework.http.HttpStatus;

import java.text.MessageFormat;
import java.util.Map;

public class AuthenticationException extends BaseCustomException {
  public AuthenticationException(ErrorCode errorCode, String details) {
    super(
        errorCode,
        errorCode.getDefaultMessage(),
        MessageFormat.format("Authentication failed: {0}", details),
        HttpStatus.UNAUTHORIZED,
        Map.of("authDetails", details));
  }
}
