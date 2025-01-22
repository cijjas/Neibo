package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ImageURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ProfessionsURIConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URI;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public class WorkerDto {
    @NotNull(groups = Null.class)
    private String user;

    @NotNull(groups = Null.class)
    @ProfessionsURIConstraint(groups = URI.class)
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

    @ImageURIConstraint(groups = URI.class)
    private String backgroundPicture;

    private Links _links;

    public static WorkerDto fromWorker(Worker worker, UriInfo uriInfo) {
        final WorkerDto dto = new WorkerDto();

        dto.phoneNumber = worker.getPhoneNumber();
        dto.businessName = worker.getBusinessName();
        dto.address = worker.getAddress();
        dto.bio = worker.getBio();

        Links links = new Links();

        String neighborhoodId = String.valueOf(worker.getUser().getNeighborhood().getNeighborhoodId());
        String workerId = String.valueOf(worker.getWorkerId());
        String userId = String.valueOf(worker.getUser().getUserId());

        UriBuilder workerUri = uriInfo.getBaseUriBuilder().path(Endpoint.WORKERS).path(workerId);
        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder professionsUri = uriInfo.getBaseUriBuilder().path(Endpoint.PROFESSIONS).queryParam(QueryParameter.FOR_WORKER, workerUri.build());
        UriBuilder workerNeighborhoodsUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).queryParam(QueryParameter.WITH_WORKER, workerUri.build());
        UriBuilder reviewsUri = workerUri.clone().path(Endpoint.REVIEWS);
        UriBuilder reviewsAverageUri = reviewsUri.clone().path(Endpoint.AVERAGE);
        UriBuilder reviewsCountUri = reviewsUri.clone().path(Endpoint.COUNT);
        UriBuilder userUri = uriInfo.getBaseUriBuilder().path(Endpoint.USERS).path(userId);
        UriBuilder postsUri = neighborhoodUri.clone().path(Endpoint.POSTS).queryParam(QueryParameter.POSTED_BY, userUri.build());
        UriBuilder postsCountUri = neighborhoodUri.clone().path(Endpoint.POSTS).path(Endpoint.COUNT).queryParam(QueryParameter.POSTED_BY, userUri.build());

        links.setSelf(workerUri.build());
        links.setUser(userUri.build());
        links.setReviewsAverage(reviewsAverageUri.build());
        links.setReviewsCount(reviewsCountUri.build());
        links.setReviews(reviewsUri.build());
        links.setProfessions(professionsUri.build());
        links.setWorkerNeighborhoods(workerNeighborhoodsUri.build());
        links.setPosts(postsUri.build());
        links.setPostsCount(postsCountUri.build());
        if (worker.getBackgroundPictureId() != null) {
            String backgroundPictureId = String.valueOf(worker.getBackgroundPictureId());
            UriBuilder backgroundPictureUri = uriInfo.getBaseUriBuilder().path(Endpoint.IMAGES).path(backgroundPictureId);
            links.setBackgroundImage(backgroundPictureUri.build());
        }

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
