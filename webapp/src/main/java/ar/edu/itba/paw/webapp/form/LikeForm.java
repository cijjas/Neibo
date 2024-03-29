package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingPostConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.PostURNConstraint;

import javax.validation.constraints.NotNull;

public class LikeForm {
    @NotNull
    @PostURNConstraint
    private String postURN;

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
