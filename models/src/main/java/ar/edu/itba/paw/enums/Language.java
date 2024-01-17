package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum Language {
    ENGLISH,
    SPANISH;

    public int getId() {
        return ordinal() + 1;
    }

    public static Language fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Language ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(l -> l.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Language Not Found"));
    }
}