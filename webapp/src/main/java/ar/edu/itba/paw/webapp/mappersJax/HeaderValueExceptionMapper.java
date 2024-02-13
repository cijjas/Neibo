package ar.edu.itba.paw.webapp.mappersJax;

import ar.edu.itba.paw.models.ApiErrorDetails;
import org.glassfish.jersey.message.internal.HeaderValueException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class HeaderValueExceptionMapper implements ExceptionMapper<HeaderValueException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(HeaderValueException exception) {
        Response.Status status = Response.Status.BAD_REQUEST;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage("Invalid header value. Please check the request headers.");
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
