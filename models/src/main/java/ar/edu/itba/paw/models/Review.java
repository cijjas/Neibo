package ar.edu.itba.paw.models;

import java.util.Date;

public class Review {
    private final long reviewId;
    private final long workerId;
    private final long userId;
    private final float rating;
    private final String review;
    private final Date date;

    private Review(Builder builder) {
        this.reviewId = builder.reviewId;
        this.workerId = builder.workerId;
        this.userId = builder.userId;
        this.rating = builder.rating;
        this.review = builder.review;
        this.date = builder.date;
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getUserId() {
        return userId;
    }

    public float getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public Date getDate() {
        return date;
    }

    public long getReviewId() {
        return reviewId;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                "workerId=" + workerId +
                ", userId=" + userId +
                ", rating=" + rating +
                ", review='" + review + '\'' +
                ", date=" + date +
                '}';
    }

    public static class Builder {
        private long reviewId;
        private long workerId;
        private long userId;
        private float rating;
        private String review;
        private Date date;

        public Builder workerId(long workerId) {
            this.workerId = workerId;
            return this;
        }

        public Builder userId(long userId) {
            this.userId = userId;
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

        public Builder reviewId(long reviewId) {
            this.reviewId = reviewId;
            return this;
        }

        public Review build() {
            return new Review(this);
        }
    }
}
