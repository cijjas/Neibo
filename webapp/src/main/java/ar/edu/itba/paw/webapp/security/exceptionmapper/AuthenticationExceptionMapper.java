package ar.edu.itba.paw.webapp.security.exceptionmapper;

import ar.edu.itba.paw.models.ApiErrorDetails;
import ar.edu.itba.paw.webapp.security.exception.ExpiredTokenException;
import org.springframework.security.core.AuthenticationException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(AuthenticationException exception) {
        Response.Status status = exception instanceof ExpiredTokenException
                ? Response.Status.UNAUTHORIZED
                : Response.Status.FORBIDDEN;

        return createResponse(status, exception.getMessage());
    }

    private Response createResponse(Response.Status status, String message) {
        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage(message);
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}