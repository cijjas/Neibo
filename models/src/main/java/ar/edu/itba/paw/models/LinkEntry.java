package ar.edu.itba.paw.models;

public class LinkEntry {
    private String content;
    private String link;

    public LinkEntry() {
    }

    public LinkEntry(String linkName, String link) {
        this.content = linkName;
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String linkName) {
        this.content = linkName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
