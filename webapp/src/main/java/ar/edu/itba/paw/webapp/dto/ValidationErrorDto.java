package ar.edu.itba.paw.webapp.dto;

import javax.validation.ConstraintViolation;

public class ValidationErrorDto {

    private String message;
    private String path;

    public static ValidationErrorDto fromValidationException(final ConstraintViolation<?> ve) {
        final ValidationErrorDto errorDto = new ValidationErrorDto();

        errorDto.message = ve.getMessage();
        errorDto.path = ve.getPropertyPath().toString();

        return errorDto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
