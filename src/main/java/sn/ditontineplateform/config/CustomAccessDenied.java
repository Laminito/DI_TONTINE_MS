package sn.ditontineplateform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import sn.ditontineplateform.response.CustomResponse;

import java.io.IOException;

@Component
public class CustomAccessDenied implements AccessDeniedHandler {
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    CustomResponse errorResponse =
        CustomResponse.builder().status("ACCESS_DENIED").statusCodeValue(403).build();

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getWriter(), errorResponse);
  }
}
