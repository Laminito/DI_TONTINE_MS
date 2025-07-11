package sn.ditontineplateform.security.service.implement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sn.ditontineplateform.config.KeycloakClientConfig;
import sn.ditontineplateform.exception.AuthenticationException;
import sn.ditontineplateform.exception.CustomException;
import sn.ditontineplateform.exception.ErrorCode;
import sn.ditontineplateform.exception.ExceptionFactory;
import sn.ditontineplateform.security.dto.TokenResponse;
import sn.ditontineplateform.security.service.interfaces.AuthService;
import sn.ditontineplateform.user.dto.UserDto;
import sn.ditontineplateform.utils.JwtUtil;
import sn.ditontineplateform.utils.ResponseMessageConstants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sn.ditontineplateform.utils.RequestHeaderParser.extractUserIdFromJwt;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final Keycloak keycloak;
    private final KeycloakClientConfig keycloakClientConfig;
    private final JwtUtil jwtUtil;
    //private final EmailService emailService;
    private final ExceptionFactory exceptionFactory;

    @Value("${keycloak-client.realm}")
    private String realm;

    @Value("${fayda-app.endpoints.frontend}")
    private String frontendBaseUrl;

    /** Authentification d'un utilisateur avec extraction des informations du token JWT */
    @Override
    public TokenResponse login (String username, String password, HttpServletRequest request)
            throws CustomException {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            log.warn("Tentative de connexion sans identifiants : username='{}'", username);
            throw exceptionFactory.validationError(
                    "username/password", null, "Le nom d'utilisateur et le mot de passe sont requis");
        }

        try {
            Keycloak keycloakLogin = KeycloakBuilder.builder()
                    .serverUrl(keycloakClientConfig.getUrl())
                    .realm(keycloakClientConfig.getRealm())
                    .clientId(keycloakClientConfig.getClientId())
                    .clientSecret(keycloakClientConfig.getClientSecret())
                    .username(username)
                    .password(password)
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();

            AccessTokenResponse token = keycloakLogin.tokenManager().getAccessToken();

            if (token == null || token.getToken() == null || token.getToken().isEmpty()) {
                log.warn("Login échoué : token invalide pour user '{}'", username);
                throw exceptionFactory.invalidToken();
            }

            return TokenResponse.builder()
                    .accessToken(token.getToken())
                    .expiresIn(Math.toIntExact(token.getExpiresIn()))
                    .refreshExpiresIn(Math.toIntExact(token.getRefreshExpiresIn()))
                    .refreshToken(token.getRefreshToken())
                    .tokenType(token.getTokenType())
                    .notBeforePolicy(token.getNotBeforePolicy())
                    .sessionState(token.getSessionState())
                    .scope(token.getScope())
                    .build();

        } catch (WebApplicationException e) {
            String msg =
                    e.getResponse() != null ? e.getResponse().readEntity(String.class) : e.getMessage();
            log.warn("Erreur d'authentification Keycloak pour user '{}': {}", username, msg);

            if (msg != null) {
                if (msg.contains("401") || msg.toLowerCase().contains("invalid user credentials")) {
                    throw exceptionFactory.invalidCredentials(username);
                } else if (msg.toLowerCase().contains("account is disabled")) {
                    throw exceptionFactory.accountDisabled(username);
                } else if (msg.toLowerCase().contains("account is temporarily locked")) {
                    throw exceptionFactory.accountLocked(username);
                } else if (msg.contains("503")) {
                    throw exceptionFactory.serviceUnavailable("Keycloak");
                }
            }

            throw new AuthenticationException(
                    ErrorCode.AUTH_INVALID_CREDENTIALS, "Erreur d'authentification");

        } catch (Exception e) {
            log.error(
                    "Erreur inattendue lors de la connexion utilisateur : {} \n {}",
                    username,
                    e.getMessage()
            );
            throw exceptionFactory.serviceInternalError("Keycloak");
        }
    }

    @Override
    public void logoutUser (String userId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(userId);
            log.info("userId : {} ", userId);
            userResource.logout();

        } catch (NotFoundException e) {
            log.warn("Tentative de déconnexion pour un utilisateur inexistant : {}", userId);
            throw exceptionFactory.userNotFound(userId);

        } catch (BadRequestException e) {
            log.warn("Requête invalide lors de la déconnexion de l'utilisateur : {}", userId);
            throw exceptionFactory.badRequest("Requête de déconnexion invalide");

        } catch (Exception e) {
            throw exceptionFactory.internalServerError("Erreur lors de la déconnexion", e);
        }
    }

    @Override
    public void resetPassword (String userId, String newPassword) throws CustomException {
        try {
            CredentialRepresentation newCred = new CredentialRepresentation();
            newCred.setTemporary(false);
            newCred.setType(CredentialRepresentation.PASSWORD);
            newCred.setValue(newPassword);

            keycloak.realm(keycloakClientConfig.getRealm()).users().get(userId).resetPassword(newCred);
        } catch (NotFoundException e) {
            throw exceptionFactory.userNotFound(userId);
        } catch (Exception e) {
            log.error("Erreur lors du reset du mot de passe : {}", e.getMessage());
            throw exceptionFactory.internalServerError(
                    "Erreur lors de la réinitialisation du mot de passe", e);
        }
    }

    @Override
    public String createKeycloakUser (UserDto request) {
        RealmResource realm = keycloak.realm(keycloakClientConfig.getRealm());
        UsersResource users = realm.users();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmailVerified(false);
        user.setEnabled(true);

        Response response = users.create(user);

        int status = response.getStatus();

        if (status == 409) {
            log.warn("Conflit : un utilisateur avec ce nom d'utilisateur ou cet e-mail existe déjà.");
            throw exceptionFactory.userAlreadyExists(request.getUsername());
        }
        if (status == 503) {
            log.error("Service Keycloak indisponible (503). Veuillez réessayer plus tard.");
            throw exceptionFactory.serviceUnavailable("Keycloak");
        }
        if (status == 400) {
            log.error("Veuillez verifier les informations saisies et recommencer à nouveau.");
            throw exceptionFactory.validationError(
                    "userData", request.toString(), "Données utilisateur invalides");
        }
        if (status != 201) {
            log.error("Échec de la création dans Keycloak : {}", response.getStatusInfo());
            throw exceptionFactory.externalServiceError(
                    "Keycloak", response.getStatusInfo().getReasonPhrase());
        }

        log.info("response : {}", response.getStatusInfo());
        // Récupère l'ID Keycloak depuis l'URL de localisation
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        UserResource userResource = users.get(userId);

        // Définit le mot de passe (optionnel)
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        users.get(userId).resetPassword(credential);

        RoleResource roleResource =
                realm
                        .roles()
                        .get(
                                ResponseMessageConstants.TONTINE_ROLE_PREFIX
                                        + ResponseMessageConstants.ROLE_USER);
        try {
            RoleRepresentation role = roleResource.toRepresentation();
            userResource.roles().realmLevel().add(List.of(role));
        } catch (NotFoundException e) {
            log.error("Rôle FAYDA_ROLE_USER introuvable : {}", e.getMessage());
            throw exceptionFactory.keycloakConfigurationError("Rôle FAYDA_ROLE_USER manquant");
        }
        return userId;
    }

    @Override
    public void sendPasswordResetEmail (String email) {
        try {
            // 1. Vérifier si l'utilisateur existe
            List<UserRepresentation> users = keycloak.realm(realm).users().search(email, true);
            if (users.isEmpty()) {
                throw exceptionFactory.emailNotFound(email);
            }

            UserRepresentation user = users.getFirst();
            log.info("UserRepresentation user : {} ", user);

            // 2. Générer un token JWT contenant l'userId
            String token = jwtUtil.generateToken(user.getEmail()); // À implémenter dans jwtUtil

            // 3. Construire le lien de réinitialisation
            String resetUrl =
                    MessageFormat.format("{0}/reset-password?token={1}", frontendBaseUrl, token);
            log.info("resetUrl : {} ", resetUrl);

            // 4. Envoyer l’email avec le lien
/*            String subject = "Réinitialisation de mot de passe";
            String content =
                    MessageFormat.format(
                            "Bonjour,\n\nCliquez sur le lien suivant pour réinitialiser votre mot de passe :\n{0}",
                            resetUrl
                    );*/
         /*   emailService.sendSimpleMessage(email, subject, content);

        } catch (EmailException e) {
            throw exceptionFactory.emailServiceError(e.getMessage());*/
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de réinitialisation : {}", e.getMessage());
            throw exceptionFactory.internalServerError("EmailService", e);
        }
    }

    @Override
    public void confirmPasswordReset (HttpServletRequest request, String newPassword) {
        try {
            String userId = extractUserIdFromJwt(request);
            if (StringUtils.isEmpty(userId)) {
                throw exceptionFactory.invalidToken();
            }
            resetPassword(userId, newPassword);
        } catch (JwtException e) {
            throw exceptionFactory.invalidToken();
        } catch (Exception e) {
            log.error("Erreur lors de la confirmation du reset de mot de passe : {}", e.getMessage());
            throw exceptionFactory.internalServerError("Erreur lors de la réinitialisation", e);
        }
    }

    @Override
    public UserRepresentation getUserByIdFromKeycloak (String userId) {
        try {
            return keycloak.realm(realm).users().get(userId).toRepresentation();
        } catch (NotFoundException e) {
            throw exceptionFactory.userNotFound(userId);
        } catch (Exception e) {
            log.error(
                    "Erreur lors de la récupération de l'utilisateur : {} \n errorMessage : {}",
                    userId,
                    e.getMessage()
            );
            throw exceptionFactory.internalServerError(
                    "Erreur lors de la récupération de l'utilisateur", e);
        }
    }

    @Override
    public TokenResponse refreshToken (String refreshToken) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost post =
                    new HttpPost(
                            MessageFormat.format(
                                    "{0}/realms/{1}/protocol/openid-connect/token",
                                    keycloakClientConfig.getUrl(), keycloakClientConfig.getRealm()
                            ));

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "refresh_token"));
            params.add(new BasicNameValuePair("refresh_token", refreshToken));
            params.add(new BasicNameValuePair("client_id", keycloakClientConfig.getClientId()));
            params.add(new BasicNameValuePair("client_secret", keycloakClientConfig.getClientSecret()));

            post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != 200) {
                    String errorResponse = EntityUtils.toString(response.getEntity());
                    log.warn("Erreur lors du refresh token : status={}, body={}", statusCode, errorResponse);
                    throw exceptionFactory.invalidToken();
                }

                String responseBody = EntityUtils.toString(response.getEntity());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                return TokenResponse.builder()
                        .accessToken(jsonNode.get("access_token").asText())
                        .refreshToken(jsonNode.get("refresh_token").asText())
                        .expiresIn(jsonNode.get("expires_in").asInt())
                        .refreshExpiresIn(jsonNode.get("refresh_expires_in").asInt())
                        .tokenType(jsonNode.get("token_type").asText())
                        .notBeforePolicy(jsonNode.get("not-before-policy").asInt())
                        .sessionState(jsonNode.get("session_state").asText())
                        .scope(jsonNode.get("scope").asText())
                        .build();
            }

        } catch (IOException e) {
            log.error("Erreur I/O lors du rafraîchissement du token : {}", e.getMessage());
            throw exceptionFactory.serviceUnavailable("Keycloak");
        }
    }

    @Override
    public void addRoleToUser (String userId, String roleName) {
        log.info("userIdInkeycloak : {}", userId);
        log.info("roleName : {}", roleName);
        RealmResource realmResource = keycloak.realm(realm);
        UserResource userResource = realmResource.users().get(userId);

        RoleRepresentation roleRepresentation = realmResource.roles().get(roleName).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        log.info(
                "userResource.roles() : {}", userResource.roles().realmLevel().listEffective().toString());
    }

    @Override
    public UserRepresentation getUserByEmail (String email) {
        List<UserRepresentation> users = keycloak.realm(realm)
                .users()
                .search(email, true);

        return users.stream()
                .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Utilisateur non trouvé dans Keycloak avec l''email : {0}", email)));
    }

    @Override
    public String getUserIdByEmail (String email) {
        return getUserByEmail(email).getId();
    }

    @Override
    public List<String> getUserRolesByKeycloakId (String keycloakId) {
        RealmResource realmResource = keycloak.realm(realm);
        UserResource userResource = realmResource.users().get(keycloakId);

        return userResource.roles().realmLevel().listEffective().stream()
                .map(RoleRepresentation::getName)
                .filter(roleName -> roleName.startsWith(ResponseMessageConstants.TONTINE_ROLE_PREFIX))
                .collect(Collectors.toList());
    }
}
