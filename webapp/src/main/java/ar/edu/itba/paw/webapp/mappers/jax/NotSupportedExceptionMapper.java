package ar.edu.itba.paw.webapp.mappers.jax;

import ar.edu.itba.paw.models.ApiErrorDetails;

import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotSupportedExceptionMapper implements ExceptionMapper<NotSupportedException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(NotSupportedException exception) {
        Response.Status status = Response.Status.UNSUPPORTED_MEDIA_TYPE;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage("Unsupported media type for the requested resource.");
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
