package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.compositeKeys.SubscriptionKey;
import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;
import java.io.Serializable;

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

    public void setId(SubscriptionKey id) {
        this.id = id;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SubscriptionKey getId() {
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
        Subscription that = (Subscription) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
