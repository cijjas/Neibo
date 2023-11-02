package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class ListingForm {
    @NotBlank
    @Size(max = 100)
    private String title;

    private String price;

    private MultipartFile mainFile;

    private MultipartFile secondFile;

    private MultipartFile thirdFile;

    @NotBlank
    @Size(max = 2000)
    private String description;

    private Integer departmentId;

    private Boolean used;

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

    public MultipartFile getMainFile() {
        return mainFile;
    }

    public MultipartFile getSecondFile() {
        return secondFile;
    }

    public MultipartFile getThirdFile() {
        return thirdFile;
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

    public void setMainFile(MultipartFile mainFile) {
        this.mainFile = mainFile;
    }

    public void setSecondFile(MultipartFile secondFile) {
        this.secondFile = secondFile;
    }

    public void setThirdFile(MultipartFile thirdFile) {
        this.thirdFile = thirdFile;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDepartmentId(Integer department) {
        this.departmentId = department;
    }



    @Override
    public String toString() {
        return "ListingForm{" +
                "title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", mainFile=" + mainFile +
                ", secondFile=" + secondFile +
                ", thirdFile=" + thirdFile +
                ", description='" + description + '\'' +
                ", department=" + departmentId +
                '}';
    }
}
