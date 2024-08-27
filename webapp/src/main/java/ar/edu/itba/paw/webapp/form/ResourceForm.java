package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageURNConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ResourceForm {
    @Size(max = 1000)
    private String description;

    @ImageURNConstraint
    private String image;

    @NotNull
    @Size(min = 1, max = 64)
    private String title;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ResourceForm{" +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
