package ar.edu.itba.paw.models;


import java.util.Map;
import java.util.Set;

/**
 * API model that represents an error.
 *
 * @author cassiomolin
 */
public class ApiErrorDetails {

    private Integer status;
    private String title;
    private String message;
    private String path;
    private Set<LinkEntry> links;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<LinkEntry> getLinks() {
        return links;
    }

    public void setLinks(Set<LinkEntry> links) {
        this.links = links;
    }
}
