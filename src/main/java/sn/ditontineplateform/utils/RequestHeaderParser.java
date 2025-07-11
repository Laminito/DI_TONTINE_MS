package sn.ditontineplateform.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static sn.faydaapp.utils.ResponseMessageConstants.FAYDA_ROLE_PREFIX;

@Slf4j
public class RequestHeaderParser {

  public static String extractUserIdFromJwt(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (token == null || token.isEmpty()) {
      log.warn("Aucun token JWT trouvé dans l'en-tête");
      return null;
    }

    try {
      String[] chunks = token.split("\\.");
      String payload = new String(Base64.getDecoder().decode(chunks[1]));
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(payload);
      return jsonNode.get("sub").asText();
    } catch (Exception e) {
      log.error("Erreur lors de l'extraction du userId du JWT", e);
      return null;
    }
  }

  public static List<String> extractRolesFromJwt(HttpServletRequest request) {
    String token = request.getHeader("Authorization");

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (token == null || token.isEmpty()) {
      log.warn("Aucun token JWT trouvé dans l'en-tête");
      return Collections.emptyList();
    }

    try {
      String[] chunks = token.split("\\.");
      String payload = new String(Base64.getDecoder().decode(chunks[1]));
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(payload);

      JsonNode realmAccess = jsonNode.get("realm_access");
      if (realmAccess == null || realmAccess.get("roles") == null) {
        log.warn("Aucun champ 'roles' trouvé dans realm_access du JWT");
        return Collections.emptyList();
      }

      List<String> roles = new ArrayList<>();
      for (JsonNode role : realmAccess.get("roles")) {
        roles.add(role.asText());
      }

      return roles;

    } catch (Exception e) {
      log.error("Erreur lors de l'extraction des rôles du JWT", e);
      return Collections.emptyList();
    }
  }

  public static boolean hasRequiredRole(List<String> roles, String roleSuffix) {
    if (roles == null || roles.isEmpty()) return false;

    String expectedRole = FAYDA_ROLE_PREFIX + roleSuffix.toUpperCase();
    return roles.stream().anyMatch(role -> role.equalsIgnoreCase(expectedRole));
  }

  public static String extractEmailFromJwt(HttpServletRequest request) {
    String token = extractRawToken(request);
    if (token == null) return null;

    try {
      String[] chunks = token.split("\\.");
      String payload = new String(Base64.getDecoder().decode(chunks[1]));
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(payload);

      JsonNode emailNode = jsonNode.get("email");
      return emailNode != null ? emailNode.asText() : null;

    } catch (Exception e) {
      log.error("Erreur lors de l'extraction de l'email du JWT", e);
      return null;
    }
  }

  private static String extractRawToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      return token.substring(7);
    }
    log.warn("Aucun token JWT trouvé dans l'en-tête Authorization");
    return null;
  }
}
