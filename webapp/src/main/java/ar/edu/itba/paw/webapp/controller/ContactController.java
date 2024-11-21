package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.webapp.dto.ContactDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;


/*
 * # Summary
 *   - A Neighborhood has many Contacts
 *
 * # Use cases
 *   - An admin can create, update, delete a Contact
 *   - A user/admin list all the Contacts
 *
 * # Issues
 *   - Contacts may have to be paginated, they currently are not
 */

@Path("neighborhoods/{neighborhoodId}/contacts")
@Component
@Validated
public class ContactController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService cs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listContacts(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/contacts'", neighborhoodId);

        // Content
        final List<Contact> contacts = cs.getContacts(neighborhoodId, page, size);
        String contactsHashCode = String.valueOf(contacts.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(contactsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (contacts.isEmpty())
            return Response.noContent()
                    .tag(contactsHashCode)
                    .build();

        final List<ContactDto> contactsDto = contacts.stream()
                .map(c -> ContactDto.fromContact(c, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/contacts",
                cs.calculateContactPages(neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ContactDto>>(contactsDto) {})
                .cacheControl(cacheControl)
                .tag(contactsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findContact(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("id") @GenericIdConstraint long contactId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/contacts/{}'", neighborhoodId, contactId);

        // Content
        Contact contact = cs.findContact(contactId, neighborhoodId).orElseThrow(NotFoundException::new);
        String contactHashCode = String.valueOf(contact.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(contactHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ContactDto.fromContact(contact, uriInfo))
                .tag(contactHashCode)
                .cacheControl(cacheControl)
                .build();
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Validated(CreateValidationSequence.class)
    public Response createContact(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @Valid final ContactDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/contacts'", neighborhoodId);

        // Creation & HashCode Generation
        final Contact contact = cs.createContact(neighborhoodId, form.getContactName(), form.getContactAddress(), form.getContactPhone());
        String contactHashCode = String.valueOf(contact.hashCode());

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(contact.getContactId())).build();

        return Response.created(uri)
                .tag(contactHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Validated(UpdateValidationSequence.class)
    public Response updateContactPartially(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("id") @GenericIdConstraint final long id,
            @Valid ContactDto form
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/contacts/{}'", neighborhoodId, id);

        // Modification & HashCode Generation
        final Contact updatedContact = cs.updateContact(id, form.getContactName(), form.getContactAddress(), form.getContactPhone());
        String updatedContactHashCode = String.valueOf(updatedContact.hashCode());

        return Response.ok(ContactDto.fromContact(updatedContact, uriInfo))
                .tag(updatedContactHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    public Response deleteById(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("id") @GenericIdConstraint final long id
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/contacts/{}'", neighborhoodId, id);

        // Deletion Attempt
        if (cs.deleteContact(id))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
