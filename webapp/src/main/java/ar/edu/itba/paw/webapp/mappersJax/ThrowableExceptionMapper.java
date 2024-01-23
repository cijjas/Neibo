package ar.edu.itba.paw.webapp.mappersJax;

import ar.edu.itba.paw.models.ApiErrorDetails;

import ar.edu.itba.paw.webapp.controller.TransactionTypeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTypeController.class);

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        // Log the exception here or handle it as needed
        LOGGER.error(exception.toString());
        LOGGER.error(Arrays.toString(exception.getStackTrace()));

        exception.printStackTrace();

        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage("Internal Server Error :(((");
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
