package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Contact;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;

public class ContactDto {

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 64, groups = Basic.class)
    private String contactName;

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 64, groups = Basic.class)
    private String contactAddress;

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 20, groups = Basic.class)
    private String contactPhone;

    private Links _links;

    public static ContactDto fromContact(Contact contact, UriInfo uriInfo) {
        final ContactDto dto = new ContactDto();

        dto.contactName = contact.getContactName();
        dto.contactAddress = contact.getContactAddress();
        dto.contactPhone = contact.getContactAddress();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(contact.getNeighborhood().getNeighborhoodId()))
                .path("contact")
                .path(String.valueOf(contact.getContactId()))
                .build());
        links.setNeighborhood(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(contact.getNeighborhood().getNeighborhoodId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

}
