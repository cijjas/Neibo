package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReviewForm {
    @NotNull
    @Range(min = 0, max = 5)
    private Float rating;

    @NotBlank
    @Size(min = 0, max = 255)
    private String review;

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "ReviewForm{" +
                "rating=" + rating +
                ", review='" + review + '\'' +
                '}';
    }
}
