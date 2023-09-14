package ar.edu.itba.paw.webapp.exceptions;

public class DuplicatedNeighbor extends NeiboException{
    public DuplicatedNeighbor() {
        super("Duplicated Neighbor, one with the same mail already exists");
    }

    public DuplicatedNeighbor(String message) {
        super(message);
    }
}
