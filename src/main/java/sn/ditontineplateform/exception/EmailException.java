package sn.ditontineplateform.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class EmailException extends BaseCustomException {

  public EmailException(String userMessage, String technicalDetail) {
    super(
        ErrorCode.EMAIL_SERVICE_ERROR,
        userMessage,
        technicalDetail,
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public EmailException(String userMessage, String technicalDetail, Map<String, Object> details) {
    super(
        ErrorCode.EMAIL_SERVICE_ERROR,
        userMessage,
        technicalDetail,
        HttpStatus.INTERNAL_SERVER_ERROR,
        details);
  }
}
