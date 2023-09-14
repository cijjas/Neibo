package ar.edu.itba.paw.webapp.exceptions;

public class DuplicatedCategory extends NeiboException {
    public DuplicatedCategory() {
        super("Duplicated Category, one with the same name already exists");
    }

    public DuplicatedCategory(String message) {
        super(message);
    }
}
