package ar.edu.itba.paw.models.Entities;// Post.java
import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_postid_seq")
    @SequenceGenerator(name = "posts_postid_seq", sequenceName = "posts_postid_seq", allocationSize = 1)
    @Column(name = "postid")
    private Long postId;

    @Column(name = "title", length = 128, nullable = false)
    private String title;

    @Column(name = "description", length = 1064, nullable = false)
    private String description;

    @Column(name = "postdate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "channelid", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "postpictureid")
    private Image postPicture;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    @ManyToMany
    @JoinTable(name = "posts_tags", joinColumns = @JoinColumn(name = "postid"), inverseJoinColumns = @JoinColumn(name = "tagid"))
    private Set<Tag> tags;

    @Column(name = "likes")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "posts_users_likes", joinColumns = @JoinColumn(name = "postid"), inverseJoinColumns = @JoinColumn(name = "userid"))
    private Set<User> likedByUsers;

    Post() {
    }

    private Post(Builder builder) {
        this.postId = builder.postId;
        this.title = builder.title;
        this.description = builder.description;
        this.date = builder.date;
        this.user = builder.user;
        this.channel = builder.channel;
        this.postPicture = builder.postPicture;
        this.tags = builder.tags;
        this.likedByUsers = builder.likedByUsers;
        this.comments = builder.comments;
        this.date = new Date(System.currentTimeMillis());
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Image getPostPicture() {
        return postPicture;
    }

    public void setPostPicture(Image postPicture) {
        this.postPicture = postPicture;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<User> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(Set<User> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", channel=" + channel +
                ", postPicture=" + postPicture +
                ", tags=" + tags +
                ", likedByUsers=" + likedByUsers +
                ", comments=" + comments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return Objects.equals(postId, post.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }

    public static class Builder {
        private Long postId;
        private String title;
        private String description;
        private Date date;
        private User user;
        private Channel channel;
        private Image postPicture;
        private Set<Tag> tags;
        private Set<User> likedByUsers;
        private Set<Comment> comments;

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

        public Builder postPicture(Image postPicture) {
            this.postPicture = postPicture;
            return this;
        }

        public Builder tags(Set<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public Builder likedByUsers(Set<User> likedByUsers) {
            this.likedByUsers = likedByUsers;
            return this;
        }

        public Builder comments(Set<Comment> comments) {
            this.comments = comments;
            return this;
        }

        public Post build() {
            return new Post(this);
        }
    }
}
