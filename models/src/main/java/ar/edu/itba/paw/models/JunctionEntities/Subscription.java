package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.User;
import ar.edu.itba.paw.models.compositeKeys.SubscriptionKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "posts_users_subscriptions")
public class Subscription implements Serializable {
    @EmbeddedId
    private SubscriptionKey id;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "postid")
    private Post post;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userid")
    private User user;

    public Subscription() {
        this.id = new SubscriptionKey();
    }

    public Subscription(Post post, User user) {
        this.id = new SubscriptionKey(post.getPostId(), user.getUserId());
        this.post = post;
        this.user = user;
    }

    public SubscriptionKey getId() {
        return id;
    }

    public void setId(SubscriptionKey id) {
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
        if (!(o instanceof Subscription)) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
