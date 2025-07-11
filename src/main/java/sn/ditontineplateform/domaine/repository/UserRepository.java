package sn.ditontineplateform.domaine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sn.ditontineplateform.domaine.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    // Recherche par identifiants uniques
    Optional<User> findByEmail (String email);

    Optional<User> findByPhoneNumber (String phoneNumber);

    Optional<User> findByExternalId (String externalId);

    Optional<User> findByUserIdAndIsActiveTrue(UUID userId); // userIdKeycloak


    boolean existsByEmail (String email);

    boolean existsByPhoneNumber (String phoneNumber);

    boolean existsByExternalId (String externalId);

    Page<User> findByIsActiveTrue (Pageable pageable);

    Page<User> findByIsActiveFalse (Pageable pageable);

    Page<User> findByGender (String gender, Pageable pageable);


}