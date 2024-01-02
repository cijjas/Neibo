package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class NewNeighborhoodForm {
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NewNeighborhoodForm{" +
                "name='" + name + '\'' +
                '}';
    }
}