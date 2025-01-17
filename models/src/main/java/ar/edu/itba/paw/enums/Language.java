package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;

public enum Language {
    ENGLISH(1),
    SPANISH(2);

    private final int id;

    Language(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Language fromId(long id) {
        return Arrays.stream(values())
                .filter(l -> l.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}