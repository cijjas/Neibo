package ar.edu.itba.paw.models.Entities;

import ar.edu.itba.paw.models.compositeKeys.CategorizationKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "posts_tags")
public class Categorization implements Serializable {
    @EmbeddedId
    private CategorizationKey id;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "postid")
    private Post post;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tagid")
    private Tag tag;

    Categorization() {
        this.id = new CategorizationKey();
    }

    public Categorization(Post post, Tag tag) {
        this.id = new CategorizationKey(post.getPostId(), tag.getTagId());
        this.post = post;
        this.tag = tag;
    }

    public CategorizationKey getId() {
        return id;
    }

    public void setId(CategorizationKey id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categorization)) return false;
        Categorization that = (Categorization) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
