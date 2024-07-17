package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ShiftsURNConstraint;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class AmenityUpdateForm {
    @NotBlank
    @Size(min = 0, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*")
    private String name;

    @NotBlank
    @Size(min = 0, max = 1000)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*")
    private String description;

    @ShiftsURNConstraint
    private List<String> shiftURNs;


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

    public List<String> getShiftURNs() {
        return shiftURNs;
    }

    public void setShiftURNs(List<String> shiftURNs) {
        this.shiftURNs = shiftURNs;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                ", subject='" + name + '\'' +
                ", message='" + description + '\'' +
                '}';
    }
}

