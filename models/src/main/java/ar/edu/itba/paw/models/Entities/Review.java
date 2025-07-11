package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reviews_reviewid_seq")
    @SequenceGenerator(sequenceName = "reviews_reviewid_seq", name = "reviews_reviewid_seq", allocationSize = 1)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "workerid", referencedColumnName = "workerid")
    private Worker worker;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private User user;

    @Column(name = "rating", nullable = false)
    private float rating;

    @Column(name = "review", length = 255, nullable = false)
    private String review;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    Review() {
    }

    public Review(Builder builder) {
        this.reviewId = builder.reviewId;
        this.worker = builder.worker;
        this.user = builder.user;
        this.rating = builder.rating;
        this.review = builder.review;
        this.date = builder.date;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return Objects.equals(reviewId, review.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId);
    }

    public static class Builder {
        private Long reviewId;
        private Worker worker;
        private User user;
        private float rating;
        private String review;
        private Date date;

        public Builder worker(Worker worker) {
            this.worker = worker;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder rating(float rating) {
            this.rating = rating;
            return this;
        }

        public Builder review(String review) {
            this.review = review;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder reviewId(Long reviewId) {
            this.reviewId = reviewId;
            return this;
        }

        public Review build() {
            return new Review(this);
        }
    }
}
