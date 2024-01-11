package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.webapp.dto.ContactDto;
import ar.edu.itba.paw.webapp.form.ContactForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listContacts() {
        LOGGER.info("Listing Contacts for Neighborhood {}", neighborhoodId);
        final List<Contact> contacts = cs.getContacts(neighborhoodId);
        final List<ContactDto> contactsDto = contacts.stream()
                .map(c -> ContactDto.fromContact(c, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ContactDto>>(contactsDto){})
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createContact(@Valid final ContactForm form) {
        LOGGER.info("Creating Contact in Neighborhood {}", neighborhoodId);
        final Contact contact = cs.createContact(neighborhoodId, form.getContactName(), form.getContactAddress(), form.getContactPhone());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(contact.getContactId())).build();
        return Response.created(uri).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response updateContactPartially(
            @PathParam("id") final long id,
            @Valid final ContactForm partialUpdate) {
        LOGGER.info("Updating Contact with id {}", id);
        final Contact contact = cs.updateContact(id, partialUpdate.getContactName(), partialUpdate.getContactAddress(), partialUpdate.getContactPhone());
        return Response.ok(ContactDto.fromContact(contact, uriInfo)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        LOGGER.info("Deleting Contact with id {}", id);
        cs.deleteContact(id);
        return Response.noContent().build();
    }

}
