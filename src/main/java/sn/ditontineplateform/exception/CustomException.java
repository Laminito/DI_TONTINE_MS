package sn.ditontineplateform.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends Exception {
  private final String codeMessage;
  private final String developerMessage;
  private final Exception exception;

  public CustomException(String codeMessage, String message, String developerMessage) {
    super(message);
    this.codeMessage = codeMessage;
    this.developerMessage = developerMessage;
    this.exception = null;
  }

  public CustomException(String codeMessage, Exception exception) {
    super(exception.getMessage());
    this.codeMessage = codeMessage;
    this.exception = exception;
    this.developerMessage = "An exception occurred in service layer.";
  }
}
