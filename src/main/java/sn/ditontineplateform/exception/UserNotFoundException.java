package sn.ditontineplateform.exception;

import org.springframework.http.HttpStatus;

import java.text.MessageFormat;
import java.util.Map;

public class UserNotFoundException extends BaseCustomException {
  public UserNotFoundException(String userId) {
    super(
        ErrorCode.USER_NOT_FOUND,
        "L'utilisateur demandé est introuvable",
        MessageFormat.format("User with ID ''{0}'' not found in database", userId),
        HttpStatus.NOT_FOUND,
        Map.of("userId", userId));
  }

  public UserNotFoundException(String userMessage, String developerMessage) {
    super(ErrorCode.USER_NOT_FOUND, userMessage, developerMessage, HttpStatus.NOT_FOUND);
  }
}
