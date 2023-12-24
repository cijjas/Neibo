package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Review;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class ReviewDto {

    private float rating;
    private String review;
    private Date date;
    private URI self;
    private URI worker;
    private URI user;

    public static ReviewDto fromReview(Review review, UriInfo uriInfo){
        final ReviewDto dto = new ReviewDto();

        dto.rating = review.getRating();
        dto.review = review.getReview();
        dto.date = review.getDate();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("reviews")
                .path(String.valueOf(review.getReviewId()))
                .build();
        dto.worker = uriInfo.getBaseUriBuilder()
                .path("workers")
                .path(String.valueOf(review.getWorker().getWorkerId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(review.getUser().getUserId()))
                .build();

        return dto;
    }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

}
