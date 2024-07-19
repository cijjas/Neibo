package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.Entities.Worker;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class WorkerDto {

    private String phoneNumber;
    private String businessName;
    private String address;
    private String bio;
    private Links _links;

    public static WorkerDto fromWorker(Worker worker, UriInfo uriInfo) {
        final WorkerDto dto = new WorkerDto();

        dto.phoneNumber = worker.getPhoneNumber();
        dto.businessName = worker.getBusinessName();
        dto.address = worker.getAddress();
        dto.bio = worker.getBio();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(worker.getWorkerId()))
                .build();
        links.setSelf(self);
        links.setUserRole(uriInfo.getBaseUriBuilder()
                .path("user-roles")
                .path(String.valueOf(worker.getUser().getRole().getId()))
                .build());
        links.setUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(worker.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(worker.getUser().getUserId()))
                .build());
        if (worker.getBackgroundPictureId() != null) {
            links.setBackgroundPicture(uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(worker.getBackgroundPictureId()))
                    .build());
        }
        links.setReviews(
                uriInfo.getBaseUriBuilder()
                        .path("workers")
                        .path(String.valueOf(worker.getWorkerId()))
                        .path("reviews")
                        .build()
        );
        links.setProfessions(
                uriInfo.getBaseUriBuilder()
                        .path("professions")
                        .queryParam("workerId", self)
                        .build()
        );
        links.setWorkerNeighborhoods(
                uriInfo.getBaseUriBuilder()
                        .path("neighborhoods")
                        .queryParam("withWorker", self)
                        .build()
        );
        dto.set_links(links);
        return dto;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
