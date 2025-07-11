package sn.ditontineplateform.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse {
  private String status;
  private int statusCodeValue;
  private String code;                    // Code d'erreur structuré
  private String message;                 // Message utilisateur final
  private String developerMessage;        // Message technique pour les développeurs
  private Object data;                    // Données (null en cas d'erreur)
  private LocalDateTime timestamp;        // Horodatage
  private String traceId;                 // ID de trace pour le debugging
  private List<ValidationError> validationErrors; // Erreurs de validation détaillées

  @Data
  @AllArgsConstructor
  public static class ValidationError {
    private String field;
    private String rejectedValue;
    private String message;
  }
}
