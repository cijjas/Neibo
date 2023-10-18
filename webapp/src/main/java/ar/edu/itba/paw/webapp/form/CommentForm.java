package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class CommentForm {

    @NotBlank
    @Size(max = 255)
    private String comment;


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
