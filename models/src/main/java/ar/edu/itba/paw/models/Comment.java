package ar.edu.itba.paw.models;

import java.util.Date;

public class Comment {
    private final long commentId;
    private final String comment;
    private final Date date;
    private final long neighborId; // Represent neighbor as a long
    private final long postId; // Represent postId as a long

    private Comment(Builder builder) {
        this.commentId = builder.commentId;
        this.comment = builder.comment;
        this.date = builder.date;
        this.neighborId = builder.neighborId; // Change the type to long
        this.postId = builder.postId; // Change the type to long
    }

    public static class Builder {
        private long commentId;
        private String comment;
        private Date date;
        private long neighborId; // Change the type to long
        private long postId; // Change the type to long

        public Builder commentId(long commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder neighborId(long neighborId) { // New method to set neighborId
            this.neighborId = neighborId;
            return this;
        }

        public Builder postId(long postId) { // New method to set postId
            this.postId = postId;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }

    public long getCommentId() {
        return commentId;
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public long getNeighborId() { // New getter for neighborId
        return neighborId;
    }

    public long getPostId() { // New getter for postId
        return postId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                ", neighborId=" + neighborId +
                ", postId=" + postId +
                '}';
    }
}
