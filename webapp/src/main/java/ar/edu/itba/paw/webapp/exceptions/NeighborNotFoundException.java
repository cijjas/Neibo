package ar.edu.itba.paw.webapp.exceptions;

public class NeighborNotFoundException extends NeiboException {
    public NeighborNotFoundException() {
        super("Neighbor Not Found");
    }

    public NeighborNotFoundException(String message) {
        super(message);
    }
}

