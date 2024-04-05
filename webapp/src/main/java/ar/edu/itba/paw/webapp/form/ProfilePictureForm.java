package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ImageURNConstraint;
import org.springframework.web.multipart.MultipartFile;

public class ProfilePictureForm {

    @ImageURNConstraint
    private String imageURN;

    public String getImageURN() {
        return imageURN;
    }

    public void setImageURN(String imageURN) {
        this.imageURN = imageURN;
    }

    @Override
    public String toString() {
        return "ResourceForm{" +
                '}';
    }
}
