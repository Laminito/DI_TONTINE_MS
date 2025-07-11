package sn.ditontineplateform.domaine.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.ditontineplateform.domaine.dto.UserDto;
import sn.ditontineplateform.domaine.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    /**
     * Récupère la liste paginée de tous les utilisateurs.
     *
     * @param pageable les informations de pagination
     * @return une page d'utilisateurs
     */
    Page<User> getAllUsers (Pageable pageable);

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id l'identifiant de l'utilisateur
     * @return l'utilisateur trouvé ou un Optional vide
     */
    Optional<User> getUserById (UUID id);

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param keycloakId l'identifiant de l'utilisateur
     * @return l'utilisateur trouvé ou un Optional vide
     */
    Optional<User> getUserByExternalId (String keycloakId);

    /**
     * Crée un nouvel utilisateur.
     *
     * @param user l'objet utilisateur à enregistrer
     * @return l'utilisateur enregistré
     */
    User createUser (User user);

    /**
     * Met à jour les informations d'un utilisateur existant.
     *
     * @param id l'identifiant de l'utilisateur à modifier
     * @param user les nouvelles données utilisateur
     * @return l'utilisateur mis à jour
     */
    /*  User updateUser(UUID id, User user);*/

    /**
     * Supprime un utilisateur par son identifiant.
     *
     * @param id l'identifiant de l'utilisateur à supprimer
     */
    void deleteUser (UUID id);

    User activateUser (UUID id);

    User updateUser (UUID id, UserDto dto);

    Optional<User> getUserByEmail (String email);
}