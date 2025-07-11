package sn.ditontineplateform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import sn.ditontineplateform.response.CustomResponse;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ExceptionFactory {

    public UserNotFoundException userNotFound (String userId) {
        return new UserNotFoundException(userId);
    }

    public AuthenticationException tokenExpired () {
        return new AuthenticationException(ErrorCode.AUTH_TOKEN_EXPIRED, "JWT token has expired");
    }

    public AuthenticationException invalidCredentials (String username) {
        return new AuthenticationException(
                ErrorCode.AUTH_INVALID_CREDENTIALS,
                MessageFormat.format("Invalid credentials for user: {0}", username)
        );
    }

    public AuthenticationException accountDisabled (String username) {
        return new AuthenticationException(
                ErrorCode.AUTH_ACCOUNT_DISABLED,
                MessageFormat.format("Account disabled for user: {0}", username)
        );
    }

    public AuthenticationException accountLocked (String username) {
        return new AuthenticationException(
                ErrorCode.AUTH_ACCOUNT_LOCKED,
                MessageFormat.format("Account temporarily locked for user: {0}", username)
        );
    }

    public ValidationException validationError(String field, String rejectedValue, String message) {
        return new ValidationException(
                List.of(new CustomResponse.ValidationError(field, rejectedValue, message)));
    }

    public BaseCustomException badRequest (String message) {
        return new BaseCustomException(
                ErrorCode.BAD_REQUEST, "La requête est invalide", message, HttpStatus.BAD_REQUEST) {
        };
    }

    public BaseCustomException internalServerError (String context, Exception e) {
        return new BaseCustomException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Une erreur interne est survenue. Veuillez réessayer plus tard.",
                MessageFormat.format(
                        "Erreur inattendue dans le contexte \"{0}\" : {1}", context, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of("context", context)
        ) {
        };
    }

    public BaseCustomException serviceUnavailable (String serviceName) {
        return new BaseCustomException(
                ErrorCode.SERVICE_UNAVAILABLE,
                "Service temporairement indisponible",
                MessageFormat.format("{0} service is currently unavailable", serviceName),
                HttpStatus.SERVICE_UNAVAILABLE,
                Map.of("service", serviceName)
        ) {
        };
    }

    public BaseCustomException serviceInternalError (String serviceName) {
        return new BaseCustomException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Le service est temporairement indisponible. Veuillez réessayer ultérieurement.",
                MessageFormat.format(
                        "Le service interne nommé \"{0}\" est actuellement indisponible. Vérifiez sa connectivité ou son état.",
                        serviceName
                ),
                HttpStatus.SERVICE_UNAVAILABLE,
                Map.of("service", serviceName)
        ) {
        };
    }

    public BaseCustomException externalServiceError (String serviceName, String reason) {
        return new BaseCustomException(
                ErrorCode.EXTERNAL_SERVICE_ERROR,
                "Une erreur est survenue avec un service externe. Veuillez réessayer plus tard.",
                MessageFormat.format(
                        "Le service externe \"{0}\" a échoué avec la raison : {1}", serviceName, reason),
                HttpStatus.BAD_GATEWAY,
                Map.of("service", serviceName, "reason", reason)
        ) {
        };
    }

    public BaseCustomException keycloakConfigurationError (String detail) {
        return new BaseCustomException(
                ErrorCode.KEYCLOAK_CONFIGURATION_ERROR,
                "Erreur de configuration dans Keycloak",
                MessageFormat.format("Erreur de configuration détectée dans Keycloak : {0}", detail),
                HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of("source", "Keycloak", "detail", detail)
        ) {
        };
    }

    public BaseCustomException userAlreadyExists (String identifier) {
        return new BaseCustomException(
                ErrorCode.USER_ALREADY_EXISTS,
                "Un utilisateur avec ces informations existe déjà",
                MessageFormat.format("User already exists with identifier: {0}", identifier),
                HttpStatus.CONFLICT,
                Map.of("identifier", identifier)
        ) {
        };
    }


    public BaseCustomException accessDenied (String message) {
        return new BaseCustomException(
                ErrorCode.ACCESS_DENIED,
                "Accès refusé",
                message != null ? message : "Vous n'avez pas les droits nécessaires pour accéder à cette ressource",
                HttpStatus.FORBIDDEN,
                Map.of()
        ) {
        };
    }


    public BaseCustomException locationNotFound (UUID locationId) {
        return new BaseCustomException(
                ErrorCode.LOCATION_NOT_FOUND,
                "Localisation non trouvée",
                MessageFormat.format("Aucune localisation trouvée avec l’ID: {0}", locationId),
                HttpStatus.NOT_FOUND,
                Map.of("locationInfoId", locationId)
        ) {
        };
    }

    public BaseCustomException invalidToken () {
        return new BaseCustomException(
                ErrorCode.AUTH_INVALID_TOKEN,
                "Token d'authentification invalide",
                "JWT token validation failed",
                HttpStatus.UNAUTHORIZED
        ) {
        };
    }

    public BaseCustomException emailServiceError (String technicalDetail) {
        return new EmailException(
                "Une erreur est survenue lors de l'envoi de l'email", technicalDetail);
    }

    public BaseCustomException emailServiceError (
            String technicalDetail, Map<String, Object> details
    ) {
        return new EmailException(
                "Une erreur est survenue lors de l'envoi de l'email", technicalDetail, details);
    }

    public BaseCustomException emailNotFound (String email) {
        return new BaseCustomException(
                ErrorCode.USER_NOT_FOUND,
                "Aucun compte associé à cette adresse email",
                MessageFormat.format("No user found with email: {0}", email),
                HttpStatus.NOT_FOUND,
                Map.of("email", email)
        ) {
        };
    }

    public BaseCustomException requestNotFound (UUID requestId) {
        return new BaseCustomException(
                ErrorCode.REQUEST_NOT_FOUND,
                "Aucune demande trouvé",
                MessageFormat.format("No request found for requestId : {0}", requestId),
                HttpStatus.NOT_FOUND,
                Map.of("requestId", requestId)
        ) {
        };
    }

    public BaseCustomException requestAlreadyBeenProcessed (String status) {
        return new BaseCustomException(
                ErrorCode.REQUEST_HAS_ALREADY_BEEN_PROCESSED,
                "Cette demande a déjà été traitée",
                MessageFormat.format("This request has already been processed : {0}", status),
                HttpStatus.PROCESSING,
                Map.of("status", status)
        ) {
        };
    }

    public BaseCustomException userNotInDahira (UUID userId, UUID dahiraId) {
        return new BaseCustomException(
                ErrorCode.AUTH_FORBIDDEN,
                "Vous n'appartenez pas à cette Dahira",
                MessageFormat.format(
                        "L'utilisateur {0} ne fait pas partie de la Dahira {1}", userId, dahiraId),
                HttpStatus.FORBIDDEN,
                Map.of("userId", userId, "dahiraId", dahiraId)
        ) {
        };
    }

    public BaseCustomException userInvalidRole (String requiredRole, String actualRole) {
        return new BaseCustomException(
                ErrorCode.USER_INVALID_ROLE,
                "Votre rôle ne vous permet pas d’effectuer cette action",
                MessageFormat.format(
                        "Rôle requis: {0}, mais l'utilisateur a: {1}", requiredRole, actualRole),
                HttpStatus.FORBIDDEN,
                Map.of("requiredRole", requiredRole, "actualRole", actualRole)
        ) {
        };
    }

    public BaseCustomException userAlreadyHasMouqadam (UUID userId) {
        return new BaseCustomException(
                ErrorCode.USER_ALREADY_HAS_MOUQADAM,
                "L'utilisateur a déjà un Mouqadam",
                MessageFormat.format("L'utilisateur {0} a déjà un Mouqadam assigné", userId),
                HttpStatus.CONFLICT,
                Map.of("userId", userId)
        ) {
        };
    }

    public BaseCustomException userRequestAlreadyPending (UUID userId, String requestType) {
        return new BaseCustomException(
                ErrorCode.USER_REQUEST_ALREADY_PENDING,
                "Une demande similaire est déjà en attente",
                MessageFormat.format(
                        "L'utilisateur {0} a déjà une demande de type {1} en attente de traitement",
                        userId, requestType
                ),
                HttpStatus.CONFLICT,
                Map.of("userId", userId, "requestType", requestType)
        ) {
        };
    }
}
