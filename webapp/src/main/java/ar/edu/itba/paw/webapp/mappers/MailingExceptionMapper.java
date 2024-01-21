package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.MailingException;
import ar.edu.itba.paw.models.ApiErrorDetails;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MailingExceptionMapper  implements ExceptionMapper<MailingException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(MailingException exception) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage(exception.getMessage());
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
