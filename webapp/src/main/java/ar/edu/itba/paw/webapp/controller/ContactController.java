package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.webapp.dto.ContactDto;
import ar.edu.itba.paw.webapp.form.ContactForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("neighborhoods/{neighborhoodId}/contacts")
@Component
public class ContactController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService cs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listContacts(
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/contacts'", neighborhoodId);

        // Check Caching
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Fresh Copy
        final List<Contact> contacts = cs.getContacts(neighborhoodId);
        if (contacts.isEmpty())
            return Response.noContent().build();
        final List<ContactDto> contactsDto = contacts.stream()
                .map(c -> ContactDto.fromContact(c, uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<ContactDto>>(contactsDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findContact(
            @PathParam("id") long contactId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/contacts/{}'", neighborhoodId, contactId);

        // Fetch
        Contact contact = cs.findContact(contactId, neighborhoodId).orElseThrow(NotFoundException::new);

        // Check Caching
        EntityTag entityTag = new EntityTag(contact.getVersion().toString());
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Fresh Copy
        return Response.ok(ContactDto.fromContact(contact, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response createContact(@Valid final ContactForm form) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/contacts'", neighborhoodId);
        final Contact contact = cs.createContact(neighborhoodId, form.getContactName(), form.getContactAddress(), form.getContactPhone());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(contact.getContactId())).build();
        return Response.created(uri).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response updateContactPartially(
            @PathParam("id") final long id,
            @Valid final ContactForm partialUpdate) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/contacts/{}'", neighborhoodId, id);
        final Contact contact = cs.updateContact(id, partialUpdate.getContactName(), partialUpdate.getContactAddress(), partialUpdate.getContactPhone());
        return Response.ok(ContactDto.fromContact(contact, uriInfo)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response deleteById(@PathParam("id") final long id) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/contacts/{}'", neighborhoodId, id);
        if(cs.deleteContact(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
