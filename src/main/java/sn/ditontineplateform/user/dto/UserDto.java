package sn.ditontineplateform.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private UUID userId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String externalId;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private boolean isActive;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;
    private String photoUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Instant lastLogin;
    private List<String> roles;
    private LocationDto location;
}
