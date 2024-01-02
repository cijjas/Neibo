package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class LikeForm {
    @NotNull
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
