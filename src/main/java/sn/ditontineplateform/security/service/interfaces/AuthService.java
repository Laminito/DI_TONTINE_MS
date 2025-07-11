package sn.ditontineplateform.security.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.keycloak.representations.idm.UserRepresentation;
import sn.ditontineplateform.exception.CustomException;
import sn.ditontineplateform.security.dto.TokenResponse;
import sn.ditontineplateform.domaine.dto.UserDto;

import java.util.List;

public interface AuthService {

    /**
     * Authentifie un utilisateur et retourne un résultat contenant le token et les informations utilisateur
     *
     * @param username nom d'utilisateur
     * @param password mot de passe
     * @return un objet AuthenticationResultDTO contenant le token et les informations utilisateur
     * @throws CustomException en cas d'erreur d'authentification
     */
    TokenResponse login (String username, String password, HttpServletRequest request) throws CustomException;

    void logoutUser (String userId);

    void resetPassword (String userId, String newPassword) throws CustomException;

    String createKeycloakUser (UserDto request);

    void sendPasswordResetEmail (String email) throws CustomException;

    void confirmPasswordReset (HttpServletRequest request, String newPassword) throws CustomException;

    UserRepresentation getUserByIdFromKeycloak (String userId);

    TokenResponse refreshToken (String refreshToken);

    void addRoleToUser (String userId, String roleName);

    UserRepresentation getUserByEmail (String email);

    String getUserIdByEmail (String email);

    List<String> getUserRolesByKeycloakId (String keycloakId);

}
