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
        // Check if there is a FORBIDDEN violation
        boolean containsForbidden = exception.getConstraintViolations().stream()
                .anyMatch(violation -> "FORBIDDEN".equals(violation.getMessage()));

        if (containsForbidden) {
            // Build a 403 FORBIDDEN response
            ApiErrorDetails errorDetails = new ApiErrorDetails();
            errorDetails.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            errorDetails.setTitle(Response.Status.FORBIDDEN.getReasonPhrase());
            errorDetails.setMessage("You are not allowed to perform this operation.");
            errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorDetails)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Otherwise, handle as a BAD_REQUEST (400)
        Response.Status status = Response.Status.BAD_REQUEST;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage("The form contains invalid data.");

        // Extract details of each constraint violation
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        List<ErrorDetail> errorDetailsList = new ArrayList<>();

        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String lastSection = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);

            errorDetailsList.add(new ErrorDetail(
                    lastSection,
                    violation.getMessage().substring(0, 1).toUpperCase() + violation.getMessage().substring(1)
            ));
        }

        errorDetails.setErrors(errorDetailsList);
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status)
                .entity(errorDetails)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
