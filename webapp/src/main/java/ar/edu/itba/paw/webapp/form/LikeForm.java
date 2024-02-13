package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingPostConstraint;

import javax.validation.constraints.NotNull;

public class LikeForm {
    @NotNull
    @ExistingPostConstraint
    private Long postId;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "LikeForm{" +
                "postId='" + postId + '\'' +
                '}';
    }
}
