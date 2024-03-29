package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.models.ApiErrorDetails;

import javax.validation.ValidationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ValidationExceptionMapper  implements ExceptionMapper<ValidationException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ValidationException exception) {
        Response.Status status = Response.Status.BAD_REQUEST;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage(exception.getMessage());
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        exception.printStackTrace();

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
