package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ChannelURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ImageURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class PublishForm {
    @NotBlank
    @Size(min = 0, max = 100)
    private String subject;

    @NotBlank
    @Size(min = 0, max = 2000)
    private String message;

    @TagsConstraint
    private List<String> tags;

    @ImageURNConstraint
    private String postImageURN;

    @ChannelURNConstraint
    private String channelURN;

    public String getChannelURN() {
        return channelURN;
    }

    public void setChannelURN(String channelURN) {
        this.channelURN = channelURN;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
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
                ", tags='" + tags + '\'' +
                '}';
    }
}
