package ar.edu.itba.paw.models.Entities;

import ar.edu.itba.paw.models.compositeKeys.LikeKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "posts_users_likes")
public class Like implements Serializable {
    @EmbeddedId
    private LikeKey id;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "postid")
    private Post post;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userid")
    private User user;

    Like() {
        this.id = new LikeKey();
    }

    public Like(Post post, User user) {
        this.id = new LikeKey(post.getPostId(), user.getUserId());
        this.post = post;
        this.user = user;
    }

    public LikeKey getId() {
        return id;
    }

    public void setId(LikeKey id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Like)) return false;
        Like like = (Like) o;
        return Objects.equals(id, like.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
