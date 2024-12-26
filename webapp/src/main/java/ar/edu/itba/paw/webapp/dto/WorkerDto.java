package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.validation.constraints.form.ImageURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProfessionsURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class WorkerDto {
    @NotNull(groups = Null.class)
    private String user;

    @NotNull(groups = Null.class)
    @ProfessionsURNConstraint(groups = URN.class)
    private List<String> professions;

    @NotNull(groups = Null.class)
    @Size(min = 1, max = 64, groups = Basic.class)
    @Pattern(regexp = "^[0-9+\\- ]*", groups = Basic.class)
    private String phoneNumber;

    @NotNull(groups = Null.class)
    @Size(min = 1, max = 128, groups = Basic.class)
    @Pattern(regexp = "^[a-zA-Z0-9 -]*", groups = Basic.class)
    private String businessName;

    @NotNull(groups = Null.class)
    @Size(min = 1, max = 128, groups = Basic.class)
    @Pattern(regexp = "^[a-zA-Z0-9 -]*", groups = Basic.class)
    private String address;

    @Size(max = 1000, groups = Basic.class)
    private String bio;

    @ImageURNConstraint(groups = URN.class)
    private String backgroundPicture;

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
        links.setUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(worker.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(worker.getUser().getUserId()))
                .build());
        if (worker.getBackgroundPictureId() != null) {
            links.setBackgroundImage(uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(worker.getBackgroundPictureId()))
                    .build());
        }
        links.setReviewsAverage(
                uriInfo.getBaseUriBuilder()
                        .path("workers")
                        .path(String.valueOf(worker.getWorkerId()))
                        .path("reviews")
                        .path("average")
                        .build()
        );
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
                        .queryParam("forWorker", self)
                        .build()
        );
        links.setWorkerNeighborhoods(
                uriInfo.getBaseUriBuilder()
                        .path("neighborhoods")
                        .queryParam("withWorker", self)
                        .build()
        );
        links.setPosts(
                uriInfo.getBaseUriBuilder()
                        .path("neighborhoods")
                        .path(String.valueOf(worker.getUser().getNeighborhood().getNeighborhoodId()))
                        .path("posts")
                        .queryParam("postedBy",
                                uriInfo.getBaseUriBuilder()
                                        .path("neighborhoods")
                                        .path(String.valueOf(worker.getUser().getNeighborhood().getNeighborhoodId()))
                                        .path("users")
                                        .path(String.valueOf(worker.getUser().getUserId())))
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getProfessions() {
        return professions;
    }

    public void setProfessions(List<String> professions) {
        this.professions = professions;
    }

    public String getBackgroundPicture() {
        return backgroundPicture;
    }

    public void setBackgroundPicture(String backgroundPicture) {
        this.backgroundPicture = backgroundPicture;
    }
}
