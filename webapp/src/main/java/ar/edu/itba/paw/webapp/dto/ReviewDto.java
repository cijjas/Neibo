package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNInReviewFormConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class ReviewDto {
    @NotNull(groups = OnCreate.class)
    @Range(min = 0, max = 5, groups = OnCreate.class)
    private Float rating;

    @NotNull(groups = OnCreate.class)
    @Size(min = 0, max = 255, groups = OnCreate.class)
    private String review;

    @NotNull(groups = OnCreate.class)
    @UserURNInReviewFormConstraint(groups = OnCreate.class)
    private String user;

    private Date date;
    private Links _links;

    public static ReviewDto fromReview(Review review, UriInfo uriInfo) {
        final ReviewDto dto = new ReviewDto();

        dto.rating = review.getRating();
        dto.review = review.getReview();
        dto.date = review.getDate();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(review.getWorker().getWorkerId()))
                .path("reviews")
                .path(String.valueOf(review.getReviewId()))
                .build());
        links.setWorker(uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(review.getWorker().getWorkerId()))
                .build());
        links.setUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(review.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(review.getUser().getUserId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
