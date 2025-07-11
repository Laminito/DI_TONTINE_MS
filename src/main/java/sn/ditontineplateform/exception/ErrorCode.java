package sn.ditontineplateform.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // Erreurs d'authentification
  AUTH_TOKEN_EXPIRED("AUTH_001", "Token d'authentification expiré"),
  AUTH_INVALID_TOKEN("AUTH_002", "Token d'authentification invalide"),
  AUTH_UNAUTHORIZED("AUTH_003", "Accès non autorisé"),
  AUTH_FORBIDDEN("AUTH_004", "Action interdite"),
  AUTH_INVALID_CREDENTIALS("AUTH_005", "Identifiants incorrects"),
  AUTH_ACCOUNT_DISABLED("AUTH_006", "Compte utilisateur désactivé"),
  AUTH_ACCOUNT_LOCKED("AUTH_007", "Compte temporairement verrouillé"),
  AUTH_MISSING_CREDENTIALS("AUTH_008", "Identifiants manquants"),

  // Erreurs utilisateur
  USER_NOT_FOUND("USER_001", "Utilisateur introuvable"),
  USER_ALREADY_EXISTS("USER_002", "Utilisateur déjà existant"),
  USER_INACTIVE("USER_003", "Compte utilisateur inactif"),
  USER_ALREADY_ACTIVE("USER_004", "Utilisateur déjà actif"),
  USER_VALIDATION_ERROR("USER_005", "Données utilisateur invalides"),
  USER_EMAIL_NOT_FOUND("USER_006", "Aucun compte associé à cette adresse email"),
  USER_INVALID_ROLE("USER_007", "L'utilisateur n'a pas le rôle requis"),
  USER_ALREADY_HAS_MOUQADAM("USER_008", "L'utilisateur a déjà un mouqadam assigné"),


  //Erreurs demande
  REQUEST_NOT_FOUND("REQUEST_001", "Demande introuvable"),
  REQUEST_HAS_ALREADY_BEEN_PROCESSED("REQUEST_002","Cette demande a déjà été traitée"),
  REQUEST_HAS_BEEN_APPROVAL("REQUEST_003","La requete à bien été approuvée"),

  // Erreurs de demandes utilisateur
  USER_REQUEST_NOT_FOUND("REQUEST_USER_001", "Demande utilisateur introuvable"),
  USER_REQUEST_ALREADY_EXISTS("REQUEST_USER_002", "Demande déjà existante"),
  USER_REQUEST_INVALID_STATUS("REQUEST_USER_003", "Statut de demande invalide"),
  USER_REQUEST_ALREADY_PENDING("REQUEST_USER_004", "Une demande est déjà en attente pour ce type"),



  // Erreurs système
  INTERNAL_SERVER_ERROR("SYS_001", "Erreur interne du serveur"),
  SERVICE_UNAVAILABLE("SYS_002", "Service temporairement indisponible"),
  VALIDATION_ERROR("SYS_003", "Erreur de validation"),
  BAD_REQUEST("SYS_004", "Requête invalide"),
  ACCESS_DENIED("SYS_004", "Accés refusé"),

  // Erreurs externes (Keycloak, etc.)
  EXTERNAL_SERVICE_ERROR("EXT_001", "Erreur du service externe"),
  KEYCLOAK_SERVICE_ERROR("EXT_002", "Service Keycloak indisponible"),
  KEYCLOAK_CONFIGURATION_ERROR("EXT_003", "Configuration Keycloak incomplète"),
  EMAIL_SERVICE_ERROR("EXT_004", "Erreur du service d'email"),

  LOCATION_NOT_FOUND("LOC_001", "Localisation non trouvée");

  private final String code;
  private final String defaultMessage;

  ErrorCode(String code, String defaultMessage) {
    this.code = code;
    this.defaultMessage = defaultMessage;
  }
}
