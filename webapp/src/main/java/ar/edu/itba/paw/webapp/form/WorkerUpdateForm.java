package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.EmailConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ImageURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.LanguageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ProfessionsConstraint;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class WorkerUpdateForm {

    @Size(min = 0, max = 64)
    @Pattern(regexp = "^[0-9+\\- ]*")
    private String phoneNumber;

    @Pattern(regexp = "^[a-zA-Z0-9 -]*")
    @Size(min = 0, max = 128)
    private String businessName;

    @Size(max = 1000)
    private String bio;

    @Pattern(regexp = "^[a-zA-Z0-9 -]*")
    @Size(min = 0, max = 128)
    private String address;

    @ImageURNConstraint
    private String backgroundPictureURN;

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBusinessName() { return businessName; }

    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getBio() { return bio; }

    public void setBio(String bio) { this.bio = bio; }

    public String getBackgroundPictureURN() {
        return backgroundPictureURN;
    }

    public void setBackgroundPictureURN(String backgroundPictureURN) {
        this.backgroundPictureURN = backgroundPictureURN;
    }

    @Override
    public String toString() {
        return "WorkerUpdateForm{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                ", bio='" + bio + '\'' +
                ", backgroundPicture=" + backgroundPictureURN +
                '}';
    }
}
