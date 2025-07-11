package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ContactDto {

    @NotNull(groups = OnCreate.class)
    @Size(max = 64)
    private String name;

    @NotNull(groups = OnCreate.class)
    @Size(max = 128)
    private String address;

    @NotNull(groups = OnCreate.class)
    @Size(max = 32)
    private String phone;

    private Links _links;

    public static ContactDto fromContact(Contact contact, UriInfo uriInfo) {
        final ContactDto dto = new ContactDto();

        dto.name = contact.getContactName();
        dto.address = contact.getContactAddress();
        dto.phone = contact.getContactPhone();

        Links links = new Links();

        String neighborhoodId = String.valueOf(contact.getNeighborhood().getNeighborhoodId());
        String contactId = String.valueOf(contact.getContactId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder contactUri = neighborhoodUri.clone().path(Endpoint.CONTACTS).path(contactId);

        links.setSelf(contactUri.build());
        links.setNeighborhood(neighborhoodUri.build());

        dto.set_links(links);
        return dto;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
