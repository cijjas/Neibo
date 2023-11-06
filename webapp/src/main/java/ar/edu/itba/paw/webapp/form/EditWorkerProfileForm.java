package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class EditWorkerProfileForm {

    @NotBlank
    @Size(min = 0, max = 64)
    private String phoneNumber;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 -]*")
    @Size(min = 0, max = 128)
    private String businessName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 -]*")
    @Size(min = 0, max = 128)
    private String address;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 -]*")
    @Size(min = 0, max = 255)
    private String bio;

    @ImageConstraint
    private MultipartFile imageFile;

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
