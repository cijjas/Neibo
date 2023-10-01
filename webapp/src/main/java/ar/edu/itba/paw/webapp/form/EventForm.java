package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.sql.Date;

public class EventForm {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 2000)
    private String description;

    private Date date;

    @NotBlank
    private String duration;

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
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getDuration() { return duration; }

    public void setDuration(String duration) { this.duration = duration; }

    @Override
    public String toString() {
        return "PublishForm{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date='"+ date + '\'' +
                ", duration'"+duration + '\'' +
                '}';
    }
}
