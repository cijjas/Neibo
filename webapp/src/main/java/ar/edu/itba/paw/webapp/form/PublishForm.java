package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class PublishForm {
    @NotBlank
    @Size(min = 0, max = 100)
    private String subject;

    @NotBlank
    @Size(min = 0, max = 2000)
    private String message;

    @TagsURNConstraint
    private List<String> tags;

    @ImageURNConstraint
    private String image;

    @ChannelURNConstraint
    private String channel;

    @NotNull
    @UserURNAuthorizationConstraint
    private String user;


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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                "subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", tags=" + tags +
                ", image='" + image + '\'' +
                ", channel='" + channel + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
