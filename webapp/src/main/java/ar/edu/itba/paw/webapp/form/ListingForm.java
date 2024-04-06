package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.DepartmentConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.DepartmentURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ImagesURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.MultipleImagesConstraint;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ListingForm {
    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String price;

    @ImagesURNConstraint
    private String[] imageURNs;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotNull
    @DepartmentURNConstraint
    private String departmentURN;

    @NotNull
    @Range(min = 1, max = 100)
    private Long quantity;

    @NotNull
    private Boolean used;

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }


    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartmentURN() {
        return departmentURN;
    }

    public void setDepartmentURN(String departmentURN) {
        this.departmentURN = departmentURN;
    }

    public String[] getImageURNs() {
        return imageURNs;
    }

    public void setImageURNs(String[] imageURNs) {
        this.imageURNs = imageURNs;
    }

    @Override
    public String toString() {
        return "ListingForm{" +
                "title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", department=" + departmentURN +
                '}';
    }
}
