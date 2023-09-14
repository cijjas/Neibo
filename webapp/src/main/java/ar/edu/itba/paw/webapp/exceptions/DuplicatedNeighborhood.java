package ar.edu.itba.paw.webapp.exceptions;

public class DuplicatedNeighborhood extends NeiboException{
    public DuplicatedNeighborhood() {
        super("Duplicated Neighborhood, one with the same name already exists");
    }

    public DuplicatedNeighborhood(String message) {
        super(message);
    }
}
