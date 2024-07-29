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
    private List<String> tagURNs;

    @ImageURNConstraint
    private String postImageURN;

    @ChannelURNConstraint
    private String channelURN;

    @NotNull
    @UserURNAuthorizationConstraint
    private String userURN;

    public String getUserURN() {
        return userURN;
    }

    public void setUserURN(String userURN) {
        this.userURN = userURN;
    }

    public String getChannelURN() {
        return channelURN;
    }

    public void setChannelURN(String channelURN) {
        this.channelURN = channelURN;
    }

    public List<String> getTagURNs() {
        return tagURNs;
    }

    public void setTagURNs(List<String> tagURNs) {
        this.tagURNs = tagURNs;
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

    public String getPostImageURN() {
        return postImageURN;
    }

    public void setPostImageURN(String postImageURN) {
        this.postImageURN = postImageURN;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                "subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", postImageURN='" + postImageURN + '\'' +
                ", tags='" + tagURNs + '\'' +
                '}';
    }
}
