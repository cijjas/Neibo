package ar.edu.itba.paw.models.Entities;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tags_tagid_seq")
    @SequenceGenerator(name = "tags_tagid_seq", sequenceName = "tags_tagid_seq", allocationSize = 1)
    @Column(name = "tagid")
    private Long tagId;

    @Column(name = "tag", length = 64, nullable = false, unique = true)
    private String tag;

    @ManyToMany
    @JoinTable(name = "posts_tags", joinColumns = @JoinColumn(name = "tagid"), inverseJoinColumns = @JoinColumn(name = "postid"))
    private List<Post> posts;

    @Version
    @ColumnDefault("1")
    private Long version;

    public Long getVersion() {
        return version;
    }

    Tag() {
    }

    private Tag(Builder builder) {
        this.tagId = builder.tagId;
        this.tag = builder.tag;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getTagId() {
        return tagId;
    }

    public String getTag() {
        return tag;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId +
                ", tag='" + tag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(tagId, tag.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }

    public static class Builder {
        private Long tagId;
        private String tag;

        public Builder tagId(Long tagId) {
            this.tagId = tagId;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Tag build() {
            return new Tag(this);
        }
    }
}
