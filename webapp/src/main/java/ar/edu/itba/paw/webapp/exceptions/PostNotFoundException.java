package ar.edu.itba.paw.webapp.exceptions;

public class PostNotFoundException extends NeiboException{
    public PostNotFoundException() {
        super("Post Not Found");
    }

    public PostNotFoundException(String message) {
        super(message);
    }
}
