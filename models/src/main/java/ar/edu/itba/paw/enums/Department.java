package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum Department {
    ELECTRONICS,
    APPLIANCES,
    HOME_FURNITURE,
    CLOTHING_FASHION,
    HEALTH_BEAUTY,
    SPORTS_OUTDOORS,
    BOOKS_MEDIA,
    TOYS_GAMES,
    JEWELRY_ACCESSORIES,
    AUTOMOTIVE,
    GROCERIES_FOOD,
    PETS_SUPPLIES,
    HOME_IMPROVEMENT,
    GARDEN_OUTDOOR,
    OFFICE_SUPPLIES,
    BABY_KIDS,
    ARTS_CRAFTS,
    TRAVEL_LUGGAGE,
    MUSIC_INSTRUMENTS,
    TECHNOLOGY,
    OTHER,
    NONE;


    public static final List<Pair<Integer, String>> DEPARTMENT_PAIRS = Arrays.stream(values())
            .map(department -> new Pair<>(department.getId(), department.toString()))
            .collect(Collectors.toList());

    public static Department fromURLString(String urlString) {
        if (Objects.equals(urlString, "all")) {
            return Department.NONE;
        }
        for (Department department : values()) {
            if (department.toURLString().equals(urlString)) {
                return department;
            }
        }
        return null;
    }

    public static List<Pair<String, String>> getDepartmentsWithUrls() {
        return Arrays.stream(values())
                .map(department -> new Pair<>(department.toURLString(), department.name()))
                .collect(Collectors.toList());
    }

    public static List<Pair<Integer, String>> getDepartments() {
        return Arrays.stream(values())
                .map(department -> new Pair<>(department.getId(), department.name()))
                .sorted(Comparator.comparing(Pair::getValue))
                .collect(Collectors.toList());
    }

    public static Department fromId(int id) {
        return Arrays.stream(values())
                .filter(department -> department.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private String toURLString() {
        return name().toLowerCase().replace("_", "-");
    }

    public String getDepartmentUrl() {
        return toURLString();
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
