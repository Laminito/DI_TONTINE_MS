package sn.ditontineplateform.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Getter
@Setter
public class KeycloakClientConfig {

  @Value("${keycloak-client.secret}")
  private String clientSecret;

  @Value("${keycloak-client.id}")
  private String clientId;

  @Value("${keycloak-client.uri}")
  private String url;

  @Value("${keycloak-client.realm}")
  private String realm;

  @Bean
  public Keycloak getKeycloakInstance() {
    return KeycloakBuilder.builder()
        .serverUrl(url)
        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
        .realm(realm)
        .clientId(clientId)
        .clientSecret(clientSecret)
        .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
        .build();
  }

  @Bean
  public String keycloakRealm() {
    return realm;
  }
}
