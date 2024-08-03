package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.*;
        import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;

public class UpdateProductForm{
    @NotBlank
    @Size(max = 100)
    private String title;

    private Double price;

    @ImagesURNConstraint
    private String[] images;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotNull
    @DepartmentURNConstraint
    private String department;

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

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "UpdateProductForm{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", images=" + Arrays.toString(images) +
                ", description='" + description + '\'' +
                ", department='" + department + '\'' +
                ", quantity=" + quantity +
                ", used=" + used +
                '}';
    }
}
