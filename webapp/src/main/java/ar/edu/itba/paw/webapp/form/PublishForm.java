package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.io.InputStream;

public class PublishForm {
    @FormDataParam("subject")
    @NotBlank
    @Size(min = 0, max = 100)
    private String subject;

    @FormDataParam("message")
    @NotBlank
    @Size(min = 0, max = 2000)
    private String message;

    @FormDataParam("tags")
    @TagsConstraint
    private String tags;

    @FormDataParam("postImage")
    @ImageConstraint
    private InputStream postImage;

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

    public void setTags(String tags) {
        this.tags = tags;
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

    public InputStream getPostImage() {
        return postImage;
    }

    public void setPostImage(InputStream postImage) {
        this.postImage = postImage;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                "subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", imageInputStream='" + postImage + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
