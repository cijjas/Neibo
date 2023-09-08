package ar.edu.itba.paw.models;

import java.util.Date;

public class Comment {
    private final int commentId;
    private final String comment;
    private final Date date;
    private final Neighbor neighbor; // Represent neighbor as an object
    private final Post post; // Represent post as an object

    private Comment(Builder builder) {
        this.commentId = builder.commentId;
        this.comment = builder.comment;
        this.date = builder.date;
        this.neighbor = builder.neighbor;
        this.post = builder.post;
    }

    public static class Builder {
        private int commentId;
        private String comment;
        private Date date;
        private Neighbor neighbor;
        private Post post;

        public Builder commentId(int commentId) {
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

        public Builder neighbor(Neighbor neighbor) {
            this.neighbor = neighbor;
            return this;
        }

        public Builder post(Post post) {
            this.post = post;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }

    public int getCommentId() {
        return commentId;
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public Neighbor getNeighbor() {
        return neighbor;
    }

    public Post getPost() {
        return post;
    }
}
