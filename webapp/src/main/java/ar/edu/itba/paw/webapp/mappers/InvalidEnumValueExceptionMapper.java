package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.InvalidEnumValueException;
import ar.edu.itba.paw.models.ApiErrorDetails;
import ar.edu.itba.paw.models.LinkEntry;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;

@Provider
public class InvalidEnumValueExceptionMapper implements ExceptionMapper<InvalidEnumValueException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(InvalidEnumValueException exception) {
        Response.Status status = Response.Status.BAD_REQUEST;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage(exception.getMessage());
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());
        errorDetails.setLinks(addRootToLinks(exception.getLinks()));

        return Response.status(status).entity(new GenericEntity<ApiErrorDetails>(errorDetails) {
                })
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Set<LinkEntry> addRootToLinks(Set<LinkEntry> links) {
        Set<LinkEntry> appendedLinks = new HashSet<>();
        String root = uriInfo.getBaseUri().toString();

        for (LinkEntry link : links) {
            link.setLink(root + link.getLink());
            appendedLinks.add(link);
        }

        return appendedLinks;
    }


}
