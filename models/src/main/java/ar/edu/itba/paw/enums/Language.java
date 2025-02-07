package ar.edu.itba.paw.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Language {
    ENGLISH(1),
    SPANISH(2);

    private final int id;

    Language(int id) {
        this.id = id;
    }

    public static Optional<Language> fromId(long id) {
        return Arrays.stream(values())
                .filter(l -> l.getId() == id)
                .findFirst();
    }

    public int getId() {
        return id;
    }
}