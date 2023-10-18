package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PublishForm {
    @NotBlank
    @Size(max = 100)
    private String subject;

    @NotBlank
    @Size(max = 2000)
    private String message;


    @TagsConstraint
    private String tags;

    @ImageConstraint
    private MultipartFile imageFile;

    private Integer channel;

    public Integer getChannel() {
        return channel;
    }
    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getTags() {
        return tags;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile image) {
        this.imageFile = image;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", imageFile='" + imageFile + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
