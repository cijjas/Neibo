package ar.edu.itba.paw.webapp.exceptions;

public class NeighborhoodNotFoundException extends NeiboException{
    public NeighborhoodNotFoundException() {
        super("Neighborhood Not Found");
    }

    public NeighborhoodNotFoundException(String message) {
        super(message);
    }
}
