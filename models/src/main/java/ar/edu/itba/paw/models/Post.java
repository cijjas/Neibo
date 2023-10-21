package ar.edu.itba.paw.models;

import java.util.Date;
import java.util.List;

public class Post {
    private final Long postId;
    private final String title;
    private final String description;
    private final Date date;
    private final User user;
    private final Channel channel;
    private final Long postPictureId;
    private final List<Tag> tags;
    private final int likes;  // Added likes field

    private Post(Builder builder) {
        this.postId = builder.postId;
        this.title = builder.title;
        this.description = builder.description;
        this.date = builder.date;
        this.user = builder.user;
        this.channel = builder.channel;
        this.postPictureId = builder.postPictureId;
        this.tags = builder.tags;
        this.likes = builder.likes;
    }

    public Long getPostId() {
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

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public Long getPostPictureId() {
        return postPictureId;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public int getLikes() {
        return likes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", user=" + user +
                ", channel=" + channel +
                ", postPictureId=" + postPictureId +
                ", tags=" + tags +
                ", likes=" + likes +
                '}';
    }

    public static class Builder {
        private Long postId;
        private String title;
        private String description;
        private Date date;
        private User user;
        private Channel channel;
        private Long postPictureId;
        private List<Tag> tags;
        private int likes;

        public Builder postId(Long postId) {
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

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder channel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public Builder postPictureId(Long postPictureId) {
            this.postPictureId = postPictureId;
            return this;
        }

        public Builder tags(List<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public Builder likes(int likes) {
            this.likes = likes;
            return this;
        }

        public Post build() {
            return new Post(this);
        }
    }
}
