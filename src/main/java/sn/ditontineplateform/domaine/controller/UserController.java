package sn.ditontineplateform.domaine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sn.ditontineplateform.response.CustomResponse;
import sn.ditontineplateform.security.service.interfaces.AuthService;
import sn.ditontineplateform.domaine.dto.UserDto;
import sn.ditontineplateform.domaine.entity.User;
import sn.ditontineplateform.domaine.mapper.UserMapper;
import sn.ditontineplateform.domaine.service.interfaces.UserService;
import sn.ditontineplateform.utils.Constants;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@Tag(
        name = "Utilisateur",
        description =
                "Opérations sur les utilisateurs  de DI_TONTINE"
)
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthService authService;

    public UserController (
            UserService userService,
            UserMapper userMapper,
            AuthService authService
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authService = authService;
    }

    /** Lister les utilisateurs avec pagination. */
    @Operation(
            security = {@SecurityRequirement(name = "bearerAuth")},
            summary = "Lister les utilisateurs",
            description = "Retourne une liste paginée d'utilisateurs."
    )
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<CustomResponse> getAllUsers (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        log.info("request : {} ", request);
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.getAllUsers(pageable);
        Page<UserDto> userDTOs =
                users.map(
                        user -> {
                            UserDto dto = userMapper.toDto(user);
                            if (user.getExternalId() != null) {
                                List<String> roles =
                                        authService.getUserRolesByKeycloakId(user.getExternalId());
                                dto.setRoles(roles);
                            }
                            return dto;
                        });

        log.info("******************getAllUsersController****************");
        log.info("userDTOs [getAllUsers] : {}", userDTOs);
        return ResponseEntity.ok(
                CustomResponse.builder()
                        .statusCodeValue(HttpStatus.OK.value())
                        .status(Constants.Message.SUCCESS_BODY)
                        .code("USERS_RETRIEVED")
                        .message("Liste des utilisateurs récupérée avec succès")
                        .developerMessage("La liste des utilisateurs paginées a reussie avec succés")
                        .data(userDTOs)
                        .timestamp(LocalDateTime.now())
                        .traceId(UUID.randomUUID().toString())
                        .build());
    }

    /** Détails d'un utilisateur. */
    @Operation(
            security = {@SecurityRequirement(name = "bearerAuth")},
            summary = "Détails d'un utilisateur",
            description = "Récupère les informations d’un utilisateur."
    )
    @ApiResponse(responseCode = "200", description = "Utilisateur trouvé")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @GetMapping("/keycloak/{id}")
    public ResponseEntity<CustomResponse> getUserByKeycloakId (@PathVariable String id) {
        log.info("******************getUserByKeycloakId****************");
        Optional<User> userOptional = userService.getUserByKeycloakId(id);

        UserDto userDTO = userMapper.toDto(userOptional.get());
        log.info("Utilisateur trouvé userDTOs [getUserById] : {}", userDTO);
        if (userDTO.getExternalId() != null) {
            List<String> roles =
                    authService.getUserRolesByKeycloakId(userDTO.getExternalId());
            userDTO.setRoles(roles);
        }

        return ResponseEntity.ok(
                CustomResponse.builder()
                        .statusCodeValue(HttpStatus.OK.value())
                        .status(Constants.Message.SUCCESS_BODY)
                        .code("USER_RETRIEVED")
                        .message("Utilisateur récupéré avec succès")
                        .developerMessage(
                                "La récupération des details d'un utilisateurs via son id à reussie avec succés")
                        .data(userDTO)
                        .timestamp(LocalDateTime.now())
                        .traceId(UUID.randomUUID().toString())
                        .build());
    }

    /** Détails d'un utilisateur. */
    @Operation(
            security = {@SecurityRequirement(name = "bearerAuth")},
            summary = "Détails d'un utilisateur",
            description = "Récupère les informations d’un utilisateur."
    )
    @ApiResponse(responseCode = "200", description = "Utilisateur trouvé")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse> getUserById (@PathVariable UUID id) {
        log.info("******************getUserByIdController****************");
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.ok(
                    CustomResponse.builder()
                            .statusCodeValue(HttpStatus.NOT_FOUND.value())
                            .status(Constants.Message.NOT_FOUND_BODY)
                            .code("USER_NOT_FOUND")
                            .message("Utilisateur introuvable")
                            .developerMessage(
                                    "L'utilisateur recherché n'existe pas ou bien son profil est desactivé")
                            .timestamp(LocalDateTime.now())
                            .traceId(UUID.randomUUID().toString())
                            .build());
        }

        User user = userOptional.get();
        UserDto userDTO = userMapper.toDto(user);

        if (userDTO.getExternalId() != null) {
            List<String> roles =
                    authService.getUserRolesByKeycloakId(userDTO.getExternalId());
            userDTO.setRoles(roles);
        }

        log.info("Utilisateur found [getUserById] : {}", userDTO);

        return ResponseEntity.ok(
                CustomResponse.builder()
                        .statusCodeValue(HttpStatus.OK.value())
                        .status(Constants.Message.SUCCESS_BODY)
                        .code("USER_RETRIEVED")
                        .message("Utilisateur récupéré avec succès")
                        .developerMessage(
                                "La récupération des details d'un utilisateurs via son keycloakId à reussie avec succés")
                        .data(userDTO)
                        .timestamp(LocalDateTime.now())
                        .traceId(UUID.randomUUID().toString())
                        .build());
    }

    /** Créer un nouvel utilisateur. */
    @Operation(summary = "Créer un utilisateur", description = "Crée un nouvel utilisateur.")
    @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès")
    @PostMapping
    public ResponseEntity<CustomResponse> createUser (@ResponseBody UserDto userDto, HttpServletRequest request) {
        log.info("******************createUserController****************");
        log.info("userRequestDTO UserRequestDTO [createUser] : {}", userDto);
        String userIdKeycloak = authService.createKeycloakUser(userDto);
        userDto.setExternalId(userIdKeycloak);
        User user = userMapper.toEntity(userDto);
        log.info("user : {} ", user.toString());
        user.setCreatedAt(new Date().toInstant());

        User saved = userService.createUser(user);
        log.info("saved [createUser] : Utilisateur creer avec succés");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        CustomResponse.builder()
                                .statusCodeValue(HttpStatus.CREATED.value())
                                .status(Constants.Message.CREATED_BODY)
                                .code("USER_CREATED")
                                .message("Utilisateur créé avec succès")
                                .developerMessage("Une nouvelle utilisateur viens d'etre creer avec succés")
                                .data(saved)
                                .timestamp(LocalDateTime.now())
                                .traceId(UUID.randomUUID().toString())
                                .build());
    }

    /** Supprimer un utilisateur. */
    @Operation(
            security = {@SecurityRequirement(name = "bearerAuth")},
            summary = "Supprimer un utilisateur",
            description = "Supprime un utilisateur existant."
    )
    @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès")
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteUser (@PathVariable UUID id) {
        log.info("******************deleteUserController****************");
        userService.deleteUser(id);
        log.info("userId deleted successfully [deleteUser] {}:", id);
        return ResponseEntity.ok(
                CustomResponse.builder()
                        .statusCodeValue(HttpStatus.OK.value())
                        .status(Constants.Message.SUCCESS_BODY)
                        .code("USER_DELETED")
                        .message("Utilisateur supprimé avec succès")
                        .developerMessage("Le profil de ce utilisateur viens d'etre desactivé")
                        .timestamp(LocalDateTime.now())
                        .traceId(UUID.randomUUID().toString())
                        .build());
    }

    @Operation(
            security = {@SecurityRequirement(name = "bearerAuth")},
            summary = "Mettre à jour un utilisateur",
            description = "Met à jour les informations d'un utilisateur actif existant."
    )
    @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé ou inactif")
    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse> updateUser (
            @PathVariable UUID id, @RequestBody UserDto dto
    ) {
        log.info("******************updateUserController****************");
        User updated = userService.updateUser(id, dto);
        log.info("user update successfully [updateUser] {}:", updated.toString());
        return ResponseEntity.ok(
                CustomResponse.builder()
                        .status(Constants.Message.SUCCESS_BODY)
                        .statusCodeValue(HttpStatus.OK.value())
                        .code("USER_UPDATED")
                        .message("Utilisateur mis à jour avec succès")
                        .developerMessage("Les informations de ce profil viens d'etre mise a jour avec succés")
                        .data(updated)
                        .timestamp(LocalDateTime.now())
                        .traceId(UUID.randomUUID().toString())
                        .build());
    }

    @Operation(
            security = {@SecurityRequirement(name = "bearerAuth")},
            summary = "Activer un utilisateur",
            description = "Réactive un utilisateur précédemment désactivé."
    )
    @ApiResponse(responseCode = "200", description = "Utilisateur activé avec succès")
    @ApiResponse(responseCode = "404", description = "Utilisateur introuvable ou déjà actif")
    @PutMapping("/activate/{id}")
    public ResponseEntity<CustomResponse> activateUser (@PathVariable UUID id) {
        log.info("******************activateUserController****************");
        User activated = userService.activateUser(id);
        log.info("user is acitvate successfully [activateUser] {}:", activated.toString());
        return ResponseEntity.ok(
                CustomResponse.builder()
                        .statusCodeValue(HttpStatus.OK.value())
                        .status(Constants.Message.SUCCESS_BODY)
                        .code("USER_ACTIVATED")
                        .message("Utilisateur activé avec succès")
                        .developerMessage("Le profil de cet utilisateur viens d'etre activé avec succés")
                        .data(activated)
                        .timestamp(LocalDateTime.now())
                        .traceId(UUID.randomUUID().toString())
                        .build());
    }
}
