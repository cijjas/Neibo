package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class ReviewDto {
    @NotNull(groups = OnCreate.class)
    @Range(min = 0, max = 5)
    private Float rating;

    @NotNull(groups = OnCreate.class)
    @Size(max = 255)
    private String message;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    private Date creationDate;

    private Links _links;

    public static ReviewDto fromReview(Review review, UriInfo uriInfo) {
        final ReviewDto dto = new ReviewDto();

        dto.rating = review.getRating();
        dto.message = review.getReview();
        dto.creationDate = review.getDate();

        Links links = new Links();

        String workerId = String.valueOf(review.getWorker().getWorkerId());
        String reviewId = String.valueOf(review.getReviewId());

        UriBuilder workerUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKERS).path(workerId);
        UriBuilder reviewUri = workerUri.clone().path(Endpoint.REVIEWS).path(reviewId);

        links.setSelf(reviewUri.build());
        links.setWorker(workerUri.build());

        dto.set_links(links);
        return dto;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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
}
