package ar.edu.itba.paw.models.Entities;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_commentid_seq")
    @SequenceGenerator(name = "comments_commentid_seq", sequenceName = "comments_commentid_seq", allocationSize = 1)
    @Column(name = "commentid")
    private Long commentId;

    @Column(name = "comment", length = 512, nullable = false)
    private String comment;

    @Column(name = "commentdate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "postid", nullable = false)
    private Post post;

    @Version
    @ColumnDefault("1")
    private Long version;

    public Long getVersion() {
        return version;
    }

    Comment() {
    }

    private Comment(Builder builder) {
        this.commentId = builder.commentId;
        this.comment = builder.comment;
        this.date = builder.date;
        this.user = builder.user;
        this.post = builder.post;
        this.date = new java.sql.Date(System.currentTimeMillis());
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                ", user=" + user +
                ", post=" + post +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(commentId, comment.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }

    public static class Builder {
        private Long commentId;
        private String comment;
        private Date date;
        private User user;
        private Post post;

        public Builder commentId(Long commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
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

        public Builder post(Post post) {
            this.post = post;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }
}
