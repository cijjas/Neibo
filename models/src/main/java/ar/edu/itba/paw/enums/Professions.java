package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Professions {
    PLUMBER,
    ELECTRICIAN,
    POOL_MAINTENANCE,
    GARDENER,
    CARPENTER;

    public static final List<Pair<Integer, String>> PROF_PAIRS = Arrays.stream(values())
            .map(profession -> new Pair<>(profession.getId(), profession.toString()))
            .collect(Collectors.toList());

    public static List<String> getProfessionsList() {
        return Arrays.stream(values())
                .map(profession -> profession.toString().toUpperCase())
                .collect(Collectors.toList());
    }

    public static Professions fromId(int id) {
        return Arrays.stream(values())
                .filter(profession -> profession.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return name().toUpperCase();
    }

    public int getId() {
        return ordinal() + 1;
    }
}
