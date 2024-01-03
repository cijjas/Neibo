package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.models.Entities.Resource;
import ar.edu.itba.paw.webapp.dto.ContactDto;
import ar.edu.itba.paw.webapp.form.ContactForm;
import ar.edu.itba.paw.webapp.form.ResourceForm;
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

    @Autowired
    private ContactService cs;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listContacts() {
        final List<Contact> contacts = cs.getContacts(neighborhoodId);
        final List<ContactDto> contactsDto = contacts.stream()
                .map(c -> ContactDto.fromContact(c, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/contacts";

        return Response.ok(new GenericEntity<List<ContactDto>>(contactsDto){})
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createContact(@Valid final ContactForm form) {
        final Contact contact = cs.createContact(neighborhoodId, form.getContactName(), form.getContactAddress(), form.getContactPhone());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(contact.getContactId())).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        cs.deleteContact(id);
        return Response.noContent().build();
    }

}
