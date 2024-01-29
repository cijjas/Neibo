package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.models.ApiErrorDetails;
import org.springframework.dao.DataIntegrityViolationException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DataIntegrityViolationExceptionMapper implements ExceptionMapper<DataIntegrityViolationException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(DataIntegrityViolationException exception) {
        Response.Status status = Response.Status.CONFLICT;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());

        String keyName = extractKeyName(exception.getMessage());

        String customMessage = "Duplicate Key Violation on Key: [" + keyName + "]";
        errorDetails.setMessage(customMessage);
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }

    private String extractKeyName(String originalMessage) {
        int firstBracketIndex = originalMessage.indexOf("[");
        int secondBracketIndex = originalMessage.indexOf("[", firstBracketIndex + 1);

        if (firstBracketIndex != -1 && secondBracketIndex != -1) {
            return originalMessage.substring(secondBracketIndex + 1, originalMessage.indexOf("]", secondBracketIndex + 1));
        } else {
            return "UnknownKey";
        }
    }

}
