package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.ProfessionsURIConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public class WorkerDto {

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    @NotNull(groups = OnCreate.class)
    @NotEmpty(groups = OnCreate.class)
    @ProfessionsURIConstraint
    private List<String> professions;

    @NotNull(groups = OnCreate.class)
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[0-9+\\- ]*")
    private String phoneNumber;

    @NotNull(groups = OnCreate.class)
    @Size(min = 1, max = 128)
    @Pattern(regexp = "^[a-zA-Z0-9 -]*")
    private String businessName;

    @NotNull(groups = OnCreate.class)
    @Size(min = 1, max = 128)
    @Pattern(regexp = "^[a-zA-Z0-9 -]*")
    private String address;

    @Size(max = 1000)
    private String bio;

    @Pattern(regexp = URIValidator.NEIGHBORHOOD_URI_REGEX)
    private String backgroundImage;

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

        UriBuilder workerUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKERS).path(workerId);
        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder professionsUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.PROFESSIONS).queryParam(QueryParameter.FOR_WORKER, workerUri.build());
        UriBuilder workerNeighborhoodsUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).queryParam(QueryParameter.WITH_WORKER, workerUri.build());
        UriBuilder reviewsUri = workerUri.clone().path(Endpoint.REVIEWS);
        UriBuilder reviewsAverageUri = reviewsUri.clone().path(Endpoint.AVERAGE);
        UriBuilder reviewsCountUri = reviewsUri.clone().path(Endpoint.COUNT);
        UriBuilder userUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS).path(userId);
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
        if (worker.getBackgroundPicture() != null) {
            String backgroundPictureId = String.valueOf(worker.getBackgroundPicture().getImageId());
            UriBuilder backgroundPictureUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.IMAGES).path(backgroundPictureId);
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

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
}
