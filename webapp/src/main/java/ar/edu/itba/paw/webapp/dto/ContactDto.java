package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ContactDto {

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 64, groups = Basic.class)
    private String name;

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 64, groups = Basic.class)
    private String address;

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 20, groups = Basic.class)
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

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS.toString()).path(neighborhoodId);
        UriBuilder contactUri = neighborhoodUri.clone().path(Endpoint.CONTACTS.toString()).path(contactId);

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
