package ar.edu.itba.paw.webapp.mappersJax;

import ar.edu.itba.paw.models.ApiErrorDetails;
import org.springframework.security.web.firewall.RequestRejectedException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RequestRejectedExceptionMapper implements ExceptionMapper<RequestRejectedException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(RequestRejectedException exception) {
        Response.Status status = Response.Status.BAD_REQUEST;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage("The request was rejected because the URL was not normalized.");
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
