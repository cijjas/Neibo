package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingPostConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.PostURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNAuthorizationConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNConstraint;

import javax.validation.constraints.NotNull;

public class LikeForm {
    @NotNull
    @PostURNConstraint
    private String post;

    @NotNull
    @UserURNAuthorizationConstraint
    private String user;

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LikeForm{" +
                "post='" + post + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
