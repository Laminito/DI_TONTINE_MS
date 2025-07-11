package sn.ditontineplateform.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import sn.ditontineplateform.response.CustomResponse;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Getter
public class ValidationException extends BaseCustomException {
    private final List<CustomResponse.ValidationError> validationErrors;

    public ValidationException (List<CustomResponse.ValidationError> errors) {
        super(
                ErrorCode.VALIDATION_ERROR,
                "Données invalides fournies",
                MessageFormat.format("Validation failed for {0} field(s)", errors.size()),
                HttpStatus.BAD_REQUEST,
                Map.of("fieldCount", errors.size())
        );
        this.validationErrors = errors;
    }

}
