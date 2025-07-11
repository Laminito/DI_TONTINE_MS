package sn.ditontineplateform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sn.ditontineplateform.response.CustomResponse;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    CustomResponse errorResponse =
        CustomResponse.builder()
            .statusCodeValue(401)
            .status("UNAUTHORIZED")
            .message("Veuillez fournir le token après authentification!")
            .developerMessage("Veuillez verifier le token s'il est bien formé")
            .build();

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getWriter(), errorResponse);
  }
}
