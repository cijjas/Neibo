package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.compositeKeys.CategorizationKey;
import ar.edu.itba.paw.models.MainEntities.Post;
import ar.edu.itba.paw.models.MainEntities.Tag;

import javax.persistence.*;
import java.io.Serializable;

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

    public Categorization() {
        this.id = new CategorizationKey();
    }

    public Categorization(Post post, Tag tag) {
        this.id = new CategorizationKey(post.getPostId(), tag.getTagId());
        this.post = post;
        this.tag = tag;
    }

    public void setId(CategorizationKey id) {
        this.id = id;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public CategorizationKey getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorization that = (Categorization) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
