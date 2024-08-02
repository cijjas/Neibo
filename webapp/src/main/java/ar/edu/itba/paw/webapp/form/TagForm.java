package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TagForm {
    @NotNull
    @TagsConstraint
    @Size(min=1, max=20)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TagForm{" +
                "name='" + name + '\'' +
                '}';
    }
}
