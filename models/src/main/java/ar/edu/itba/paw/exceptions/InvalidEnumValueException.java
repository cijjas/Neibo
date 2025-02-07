package ar.edu.itba.paw.exceptions;

import ar.edu.itba.paw.models.LinkEntry;

import java.util.Set;

public class InvalidEnumValueException extends RuntimeException {

    private final Set<LinkEntry> validValuesLink;

    public InvalidEnumValueException(String message, Set<LinkEntry> validValuesLink) {
        super(message);
        this.validValuesLink = validValuesLink;
    }

    public Set<LinkEntry> getLinks() {
        return validValuesLink;
    }
}
