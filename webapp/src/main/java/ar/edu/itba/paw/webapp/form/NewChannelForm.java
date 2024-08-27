package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewChannelForm {
    @NotNull
    @Size(min = 1, max = 20)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NewChannelForm{" +
                "name='" + name + '\'' +
                '}';
    }
}
