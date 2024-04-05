package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageURNConstraint;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ResourceForm {
    @Size(max = 1000)
    private String description;

    @ImageURNConstraint
    private String imageURN;

    @NotNull
    @Size(min=1, max = 64)
    private String title;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURN() {
        return imageURN;
    }

    public void setImageURN(String imageURN) {
        this.imageURN = imageURN;
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
