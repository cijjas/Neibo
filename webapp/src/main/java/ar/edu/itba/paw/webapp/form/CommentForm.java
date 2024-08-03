package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNAuthorizationConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNConstraint;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class CommentForm {

    @NotBlank
    @Size(min = 0, max = 500)
    private String comment;

    @UserURNAuthorizationConstraint
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                ", comment='" + comment + '\'' +
                '}';
    }
}
