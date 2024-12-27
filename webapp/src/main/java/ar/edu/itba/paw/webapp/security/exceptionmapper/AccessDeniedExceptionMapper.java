package ar.edu.itba.paw.webapp.security.exceptionmapper;

import ar.edu.itba.paw.models.ApiErrorDetails;
import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(AccessDeniedException exception) {
        return createResponse(Response.Status.FORBIDDEN, "You don't have enough permissions to perform this action");
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