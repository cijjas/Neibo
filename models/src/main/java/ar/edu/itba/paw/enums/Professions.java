package ar.edu.itba.paw.enums;

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
                .map(profession -> profession.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public int getId() {
        return ordinal() + 1;
    }
}
