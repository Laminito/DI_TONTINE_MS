package sn.ditontineplateform.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import sn.faydaapp.dto.RequestUser;
import sn.faydaapp.exception.CustomException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static sn.faydaapp.utils.ResponseMessageConstants.FAYDA_ROLE_PREFIX;
import static sn.faydaapp.utils.ResponseMessageConstants.ROLE;

@Slf4j
public class KeycloakJwtRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  private static final String CLAIM_REALM_ACCESS = "realm_access";
  private static final String CLAIM_RESOURCE_ACCESS = "resource_access";
  private static final String CLAIM_ROLES = "roles";

  private final String kcClientId;

  public KeycloakJwtRolesConverter (String kcClientId) {
    this.kcClientId = kcClientId;
  }

  public static List<String> getCurrentUserRoles(RequestUser user, String param) throws CustomException {
    try {
      if (user == null || user.getRealm_access() == null || user.getRealm_access().getRoles() == null) {
        throw new RuntimeException(
            "Données utilisateur manquantes \nImpossible de récupérer les rôles de l'utilisateur");
      }

      List<String> roles = user.getRealm_access().getRoles();

      if (ROLE.equalsIgnoreCase(param)) {
        return roles.stream().filter(role -> role.startsWith(FAYDA_ROLE_PREFIX)).collect(Collectors.toList());
      }

      log.debug("User email: {}", user.getEmail());
      return Collections.singletonList(user.getEmail());

    } catch (Exception e) {
      log.error("Erreur lors de la récupération des rôles utilisateur : {}", e.getMessage());
      throw new RuntimeException("Impossible de récupérer les rôles de l'utilisateur", e);
    }
  }

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Map<String, Collection<String>> realmAccess = jwt.getClaim(CLAIM_REALM_ACCESS);
    Map<String, Map<String, Collection<String>>> resourceAccess = jwt.getClaim(CLAIM_RESOURCE_ACCESS);

    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

    if (realmAccess != null && !realmAccess.isEmpty()) {
      Collection<String> realmRole = realmAccess.get(CLAIM_ROLES);
      if (realmRole != null && !realmRole.isEmpty()) {
        realmRole.forEach(
            r -> {
              if (resourceAccess != null && !resourceAccess.isEmpty() && resourceAccess.containsKey(kcClientId)) {
                resourceAccess.get(kcClientId).get(CLAIM_ROLES).forEach(
                    resourceRole -> {
                      String role = String.format("%s_%s", r, resourceRole).toUpperCase(Locale.ROOT);
                      grantedAuthorities.add(new SimpleGrantedAuthority(role));
                    });
              } else {
                grantedAuthorities.add(new SimpleGrantedAuthority(r));
              }
            });
      }
    }

    return grantedAuthorities;
  }
}
