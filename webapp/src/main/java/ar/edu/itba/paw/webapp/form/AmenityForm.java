package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AmenityForm {
    @NotBlank
    @Size(min = 0, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*")
    private String name;

    @NotBlank
    @Size(min = 0, max = 1000)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*")
    private String description;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "PublishForm{" +
                ", subject='" + name + '\'' +
                ", message='" + description + '\'' +
                '}';
    }
}

