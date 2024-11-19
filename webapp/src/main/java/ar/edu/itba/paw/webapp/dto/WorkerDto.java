package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.validation.constraints.ImageURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.ProfessionsURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnUpdate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class WorkerDto {
    @NotNull(groups = OnCreate.class)
    @UserURNConstraint(groups = OnCreate.class)
    private String user;

    @NotNull(groups = OnCreate.class)
    @ProfessionsURNConstraint(groups = OnCreate.class)
    private String[] professions;

    @NotNull(groups = OnCreate.class)
    @Size(min = 1, max = 64, groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "^[0-9+\\- ]*", groups = {OnCreate.class, OnUpdate.class})
    private String phoneNumber;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = "^[a-zA-Z0-9 -]*", groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 1, max = 128, groups = {OnCreate.class, OnUpdate.class})
    private String businessName;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = "^[a-zA-Z0-9 -]*", groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 1, max = 128, groups = {OnCreate.class, OnUpdate.class})
    private String address;

    @Size(max = 1000, groups = OnUpdate.class)
    private String bio;

    @ImageURNConstraint(groups = OnUpdate.class)
    private String backgroundPicture;

    private Float averageRating;
    private Links _links;

    public static WorkerDto fromWorker(Worker worker, Float averageRating, UriInfo uriInfo) {
        final WorkerDto dto = new WorkerDto();

        dto.phoneNumber = worker.getPhoneNumber();
        dto.businessName = worker.getBusinessName();
        dto.address = worker.getAddress();
        dto.bio = worker.getBio();
        dto.averageRating = averageRating;

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(worker.getWorkerId()))
                .build();
        links.setSelf(self);
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

    public Float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Float averageRating) {
        this.averageRating = averageRating;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String[] getProfessions() {
        return professions;
    }

    public void setProfessions(String[] professions) {
        this.professions = professions;
    }

    public String getBackgroundPicture() {
        return backgroundPicture;
    }

    public void setBackgroundPicture(String backgroundPicture) {
        this.backgroundPicture = backgroundPicture;
    }
}
