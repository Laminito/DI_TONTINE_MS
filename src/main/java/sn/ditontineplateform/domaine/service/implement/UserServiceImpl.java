package sn.ditontineplateform.domaine.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sn.ditontineplateform.exception.ExceptionFactory;
import sn.ditontineplateform.domaine.dto.UserDto;
import sn.ditontineplateform.domaine.entity.User;
import sn.ditontineplateform.domaine.mapper.LocationMapper;
import sn.ditontineplateform.domaine.repository.UserRepository;
import sn.ditontineplateform.domaine.service.interfaces.UserService;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LocationMapper locationMapper;
    private final ExceptionFactory exceptionFactory;

    @Override
    public Page<User> getAllUsers (Pageable pageable) {
        return userRepository.findByIsActiveTrue(pageable);
    }

    @Override
    public Optional<User> getUserById (UUID id) {
        return userRepository.findByUserIdAndIsActiveTrue(id);
    }

    @Override
    public Optional<User> getUserByEmail (String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByKeycloakId (String keycloakId) {
        return userRepository.findByExternalId(keycloakId);
    }

    @Override
    @Transactional
    public User createUser (User user) {
        try {
            user.setCreatedAt(new Date().toInstant());
            log.info("Creating new user with email: {}", user.getEmail());
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Error while saving user", e);
            throw exceptionFactory.internalServerError("createUser", e);
        }
    }

    @Override
    @Transactional
    public void deleteUser (UUID id) {
        userRepository
                .findByUserIdAndIsActiveTrue(id)
                .ifPresentOrElse(
                        user -> {
                            user.setActive(false);
                            user.setUpdatedAt(new Date().toInstant());
                            userRepository.save(user);
                            log.info("User logically deleted: {}", id);
                        },
                        () -> {
                            log.warn("User not found for deletion: {}", id);
                            throw exceptionFactory.userNotFound(id.toString());
                        }
                );
    }

    @Override
    @Transactional
    public User activateUser (UUID id) {
        return userRepository
                .findById(id)
                .filter(user -> !user.isActive())
                .map(
                        user -> {
                            user.setActive(true);
                            user.setUpdatedAt(new Date().toInstant());
                            log.info("User activated: {}", id);
                            return userRepository.save(user);
                        })
                .orElseThrow(
                        () -> {
                            log.warn("User not found or already active: {}", id);
                            return exceptionFactory.userNotFound(id.toString());
                        });
    }

    @Override
    @Transactional
    public User updateUser (UUID id, UserDto dto) {
        return userRepository
                .findByUserIdAndIsActiveTrue(id)
                .map(
                        user -> {
                            user.setFirstName(dto.getFirstName());
                            user.setLastName(dto.getLastName());
                            user.setEmail(dto.getEmail());
                            user.setPhoneNumber(dto.getPhoneNumber());
                            user.setGender(dto.getGender());
                            user.setDateOfBirth(dto.getDateOfBirth());
                            user.setLocation(locationMapper.toEntity(dto.getLocation()));
                            user.setUpdatedAt(new Date().toInstant());
                            log.info("Updating user: {}", id);
                            return userRepository.save(user);
                        })
                .orElseThrow(
                        () -> {
                            log.warn("User not found for update: {}", id);
                            return exceptionFactory.userNotFound(id.toString());
                        });
    }


}
