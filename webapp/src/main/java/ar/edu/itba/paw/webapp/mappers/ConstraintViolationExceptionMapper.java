package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.models.ApiErrorDetails;
import ar.edu.itba.paw.models.ErrorDetail;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Response.Status status = Response.Status.BAD_REQUEST;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage("Validation error. The request contains invalid data.");

        // Extract details of each constraint violation
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();

        // Create a list to store error details
        List<ErrorDetail> errorDetailsList = new ArrayList<>();

        for (ConstraintViolation<?> violation : violations) {
            // Get the last section of the property path
            String propertyPath = violation.getPropertyPath().toString();
            String lastSection = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);

            // Add each violation to the list with the original field name
            errorDetailsList.add(new ErrorDetail(
                    lastSection,
                    violation.getMessage().substring(0, 1).toUpperCase() + violation.getMessage().substring(1)
            ));
        }

        // Set the list of error details in the ApiErrorDetails object
        errorDetails.setErrors(errorDetailsList);

        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
