package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceInReviewConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class ReviewDto {
    @NotNull(groups = Null.class)
    @Range(min = 0, max = 5, groups = Basic.class)
    private Float rating;

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 255, groups = Basic.class)
    private String message;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNReferenceInReviewConstraint(groups = Authorization.class)
    private String user;

    private Date date;

    private Links _links;

    public static ReviewDto fromReview(Review review, UriInfo uriInfo) {
        final ReviewDto dto = new ReviewDto();

        dto.rating = review.getRating();
        dto.message = review.getReview();
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
}
