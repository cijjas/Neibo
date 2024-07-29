package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingPostConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.PostURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNAuthorizationConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNConstraint;

import javax.validation.constraints.NotNull;

public class LikeForm {
    @NotNull
    @PostURNConstraint
    private String postURN;

    @NotNull
    @UserURNAuthorizationConstraint
    private String userURN;

    public @NotNull String getUserURN() {
        return userURN;
    }

    public void setUserURN(@NotNull String userURN) {
        this.userURN = userURN;
    }

    public String getPostURN() {
        return postURN;
    }

    public void setPostURN(String postURN) {
        this.postURN = postURN;
    }

    @Override
    public String toString() {
        return "LikeForm{" +
                "postURN='" + postURN + '\'' +
                '}';
    }
}
