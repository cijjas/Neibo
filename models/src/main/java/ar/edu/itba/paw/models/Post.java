package ar.edu.itba.paw.models;

import java.util.Date;

public class Post {
    private final long postId;
    private final String title;
    private final String description;
    private final Date date;
    private final Neighbor neighbor; // Represent neighbor as an object

    private Post(Builder builder) {
        this.postId = builder.postId;
        this.title = builder.title;
        this.description = builder.description;
        this.date = builder.date;
        this.neighbor = builder.neighbor;
    }

    public static class Builder {
        private long postId;
        private String title;
        private String description;
        private Date date;
        private Neighbor neighbor;

        public Builder postId(long postId) {
            this.postId = postId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
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

        public Post build() {
            return new Post(this);
        }
    }

    public long getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public Neighbor getNeighbor() {
        return neighbor;
    }
}
