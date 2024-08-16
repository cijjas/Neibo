package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.Pair;
import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Profession {
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

    public static Profession fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Profession ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Profession Not Found"));
    }

    @Override
    public String toString() {
        return name().toUpperCase();
    }

    public int getId() {
        return ordinal() + 1;
    }
}
