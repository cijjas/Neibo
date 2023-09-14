package ar.edu.itba.paw.webapp.exceptions;

public class DuplicatedChannel extends NeiboException{
    public DuplicatedChannel() {
        super("Duplicated Channel, one with the same name already exists");
    }

    public DuplicatedChannel(String message) {
        super(message);
    }
}
