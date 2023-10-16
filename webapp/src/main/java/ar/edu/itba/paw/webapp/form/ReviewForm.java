package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReviewForm {
    @NotNull
    private Float rating;

    @NotBlank
    @Size(max = 255)
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
