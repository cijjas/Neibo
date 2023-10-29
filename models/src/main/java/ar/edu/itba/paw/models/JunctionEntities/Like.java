package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.compositeKeys.LikeKey;
import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;
import java.io.Serializable;

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

    public Like() {
        this.id = new LikeKey();
    }

    public Like(Post post, User user) {
        this.id = new LikeKey(post.getPostId(), user.getUserId());
        this.post = post;
        this.user = user;
    }

    public void setId(LikeKey id) {
        this.id = id;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LikeKey getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like that = (Like) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
