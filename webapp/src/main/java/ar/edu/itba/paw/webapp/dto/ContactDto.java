package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Contact;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ContactDto {
    private String contactName;
    private String contactAddress;
    private String contactPhone;
    private URI self;
    private URI neighborhood; // localhost:8080/neighborhood/{id}

    public static ContactDto fromContact(Contact contact, UriInfo uriInfo){
        final ContactDto dto = new ContactDto();

        dto.contactName = contact.getContactName();
        dto.contactAddress = contact.getContactAddress();
        dto.contactPhone = contact.getContactAddress();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(contact.getNeighborhood().getNeighborhoodId()))
                .path("contact")
                .path(String.valueOf(contact.getContactId()))
                .build();
        dto.neighborhood = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(contact.getNeighborhood().getNeighborhoodId()))
                .build();

        return dto;
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

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(URI neighborhood) {
        this.neighborhood = neighborhood;
    }

}
