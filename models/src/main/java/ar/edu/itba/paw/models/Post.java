package ar.edu.itba.paw.models;

import java.util.Date;

public class Post {
    private final long postId;
    private final String title;
    private final String description;
    private final Date date;
    private final Neighbor neighbor;
    private final Channel channel;
    private final byte[] imageFile; // Add the imageFile attribute

    private Post(Builder builder) {
        this.postId = builder.postId;
        this.title = builder.title;
        this.description = builder.description;
        this.date = builder.date;
        this.neighbor = builder.neighbor;
        this.channel = builder.channel;
        this.imageFile = builder.imageFile; // Initialize imageFile in the constructor
    }

    public static class Builder {
        private long postId;
        private String title;
        private String description;
        private Date date;
        private Neighbor neighbor;
        private Channel channel;
        private byte[] imageFile; // Add imageFile attribute in the builder

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

        public Builder channel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public Builder imageFile(byte[] imageFile) {
            this.imageFile = imageFile; // Set the imageFile data
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

    public Channel getChannel() {
        return channel;
    }

    public byte[] getImageFile() {
        return imageFile; // Add the getter for imageFile
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", neighbor=" + neighbor +
                ", channel=" + channel +
                ", imageFile='" + imageFile + '\'' + // Include imageFile in the toString method
                '}';
    }
}
