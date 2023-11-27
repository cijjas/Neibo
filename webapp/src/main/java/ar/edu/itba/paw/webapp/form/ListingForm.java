package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.DepartmentConstraint;
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

    @MultipleImagesConstraint
    private MultipartFile[] imageFiles;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotNull
    @DepartmentConstraint
    private Integer departmentId;

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

    public Integer getDepartmentId() {
        return departmentId;
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

    public void setDepartmentId(Integer department) {
        this.departmentId = department;
    }

    public MultipartFile[] getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(MultipartFile[] imageFiles) {
        this.imageFiles = imageFiles;
    }

    @Override
    public String toString() {
        return "ListingForm{" +
                "title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", department=" + departmentId +
                '}';
    }
}
