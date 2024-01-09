package ar.edu.itba.paw.enums;

import java.util.Arrays;

public enum Language {
    ENGLISH,
    SPANISH;

    public int getId() {
        return ordinal() + 1;
    }

    public static Language fromId(int id) {
        return Arrays.stream(values())
                .filter(language -> language.getId() == id)
                .findFirst()
                .orElse(null);
    }
}