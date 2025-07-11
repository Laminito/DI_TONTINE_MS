package sn.ditontineplateform.utils;

public class ResponseMessageConstants {

  public static final String USER_DELETE_SUCCESS = "Utilisateur supprimé avec succès";

  public static final String USER_NOT_FOUND = "Utilisateur introuvable";

  public static final String USER_GET_BY_ID_SUCCESS = "Utilisateur récupéré avec succès";

  public static final String GET_ALL_USERS_SUCCESS = "Liste des utilisateurs récupérée avec succès";

  public static final String SAVE_USER_SUCCESS = "Utilisateur créé avec succès";

  public static final String USER_UPDATE_SUCCESS = "Utilisateur mis à jour avec succès";

  public static final String USER_ACTIVATE_SUCCESS = "Utilisateur activé avec succès";

  public static final String USER_ALREADY_ACTIVE = "Utilisateur déjà actif";

  public static final String USER_ALREADY_INACTIVE = "Utilisateur déjà désactivé";

  public static final String USER_DEACTIVATION_SUCCESS = "Utilisateur désactivé avec succès";

  public static final String USER_LOGGED_OUT_SUCCESS = "Utilisateur deconnecté avec succés";
  public static final String ERROR_LOGGOUT = "Erreur lors de la déconnexion";
  public static final String USER_ID_NOT_FOUND = "User ID non trouvé";
  
  // Split Token
  public static final int BEGIN_INDEX = 7;
  public static final String DATE_FIELD_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  // Profil
  public static String TONTINE_ROLE_PREFIX = "TONTINE_ROLE_";
  public static final String ROLE_ADMIN = "ADMIN";
  public static final String ROLE_USER = "USER";
  public static final String ROLE_DEFAULT = "DEFAULT";
  public static final String ROLE = "role";
  public static final String EMAIL = "email";

  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";

  public static final String BAD_REQUEST = "BAD_REQUEST";
  public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
  public static final String GENERIC_FEIGN_EXCEPTION = "GENERIC_FEIGN_EXCEPTION";
  public static final String UNEXPECTED_ERROR = "UNEXPECTED_ERROR";
  public static final String UNAUTHORIZED_ACCESS = "UNAUTHORIZED_ACCESS";

  public static final String ERROR_OF_JSON_PARSER = "ERROR_OF_JSON_PARSER";
  public static final String X_JWT_ASSERTION = "X-JWT-Assertion";

  public static final String UPDATE_PASSWORD = "UPDATE_PASSWORD";

  public static final String WELCOME_MESSAGE = "Bienvenue chez FAYDA 🎉";


  // Messages spécifiques UserRequest
  public static final String USER_REQUEST_CREATED = "Demande utilisateur créée avec succès.";
  public static final String USER_REQUEST_NOT_FOUND = "Demande utilisateur introuvable.";
  public static final String USER_REQUEST_APPROVED = "Demande utilisateur approuvée.";
  public static final String USER_REQUEST_REJECTED = "Demande utilisateur rejetée.";
  public static final String USER_REQUEST_ALREADY_EXISTS = "Une demande similaire est déjà en attente ou approuvée.";
  public static final String USER_REQUEST_DELETED = "Demande utilisateur supprimée.";

  public static final String USER_NOT_FOUND_FOR_DELETE =
      "Utilisateur non trouvé pour suppression logique";
  public static final String USER_NOT_FOUND_OR_ALREADY_ACTIVE =
      "Utilisateur non trouvé ou déjà actif";
  public static final String USER_ACTIVE_NOT_FOUND_FOR_UPDATE =
      "Utilisateur actif non trouvé pour mise à jour";

  // Messages spécifiques Dahira
  public static final String DAHIRA_NOT_FOUND = "Dahira introuvable.";
  public static final String DAHIRA_CREATED = "Dahira créée avec succès.";
  public static final String DAHIRA_UPDATED = "Dahira mise à jour avec succès.";
  public static final String DAHIRA_DELETED = "Dahira supprimée.";
  public static final String DEMAND_JOIN_DAHIRA =
      "Demande d’adhésion au Dahira soumise avec succès.";

  public static final String USER_REQUEST_LIST_RETRIEVED = "Liste des demandes récupérée avec succès.";
  public static final String USER_REQUEST_BY_REQUESTER_RETRIEVED = "Demandes du demandeur récupérées.";
  public static final String USER_REQUEST_PENDING_RETRIEVED = "Demandes en attente récupérées.";
  public static final String USER_REQUEST_BY_TYPE_RETRIEVED = "Demandes par type récupérées.";
  public static final String USER_REQUEST_BY_STATUS_RETRIEVED = "Demandes par statut récupérées.";
  public static final String USER_REQUEST_BY_DAHIRA_RETRIEVED = "Demandes associées à la Dahira récupérées.";
  public static final String USER_REQUEST_BY_TARGET_USER_RETRIEVED = "Demandes visant l'utilisateur récupérées.";

  public static final String SERVER_INDISPONIBLE =
      "Service Keycloak temporairement indisponible.\nVeuillez réessayer plus tard..";
  public static final String CONFLICT_WHEN_ADD_NEW_USER =
      "Un utilisateur avec ce nom d'utilisateur ou cet e-mail existe déjà";

  public static final String MAUVAISE_INFORMATION_SAISIE =
      "Mauvaise information saisie lors de la création dans Keycloak ";

  public static final String TARGET_USER_NOT_FOUND = "Utilisateur cible introuvable";
}
