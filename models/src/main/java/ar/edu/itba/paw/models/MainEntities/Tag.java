package ar.edu.itba.paw.models.MainEntities;

import javax.persistence.*;
import java.util.List;

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

    public Tag() {
        // Default constructor for JPA
    }

    private Tag(Builder builder) {
        this.tagId = builder.tagId;
        this.tag = builder.tag;
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

    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId +
                ", tag='" + tag + '\'' +
                '}';
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
