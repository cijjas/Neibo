package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.webapp.controller.constants.*;
import ar.edu.itba.paw.webapp.dto.ContactDto;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 *   - A Neighbor/Admin can list all the Contacts
 *   - An Admin can create, update and delete a Contact
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.CONTACTS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class ContactController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);
    private final ContactService cs;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public ContactController(ContactService cs) {
        this.cs = cs;
    }

    @GET
    public Response listContacts(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

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
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.CONTACTS),
                cs.calculateContactPages(neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ContactDto>>(contactsDto) {
                })
                .cacheControl(cacheControl)
                .tag(contactsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("{" + PathParameter.CONTACT_ID + "}")
    public Response findContact(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.CONTACT_ID) long contactId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Contact contact = cs.findContact(neighborhoodId, contactId).orElseThrow(NotFoundException::new);
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
    @Secured({UserRole.ADMINISTRATOR, UserRole.SUPER_ADMINISTRATOR})
    @Validated(CreateSequence.class)
    public Response createContact(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @NotNull ContactDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        final Contact contact = cs.createContact(neighborhoodId, createForm.getName(), createForm.getAddress(), createForm.getPhone());
        String contactHashCode = String.valueOf(contact.hashCode());

        // Resource URI
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(contact.getContactId())).build();

        return Response.created(uri)
                .tag(contactHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.CONTACT_ID + "}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @Secured({UserRole.ADMINISTRATOR, UserRole.SUPER_ADMINISTRATOR})
    @Validated(UpdateSequence.class)
    public Response updateContact(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.CONTACT_ID) long contactId,
            @Valid @NotNull ContactDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '{}'", uriInfo.getRequestUri());

        // Modification & HashCode Generation
        final Contact updatedContact = cs.updateContact(neighborhoodId, contactId, updateForm.getName(), updateForm.getAddress(), updateForm.getPhone());
        String updatedContactHashCode = String.valueOf(updatedContact.hashCode());

        return Response.ok(ContactDto.fromContact(updatedContact, uriInfo))
                .tag(updatedContactHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.CONTACT_ID + "}")
    @Secured({UserRole.ADMINISTRATOR, UserRole.SUPER_ADMINISTRATOR})
    public Response deleteContact(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.CONTACT_ID) long contactId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (cs.deleteContact(neighborhoodId, contactId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
