package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ContactService;
import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.webapp.dto.ContactDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
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

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        cs.deleteContact(id);
        return Response.noContent().build();
    }

}
