package ar.edu.itba.paw.enums;

import java.util.Arrays;
import java.util.List;
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

    public static List<String> getDepartmentsList() {
        return Arrays.stream(values())
                .map(Department::name)
                .collect(Collectors.toList());
    }

    public static List<Pair<Integer, String>> getDepartments() {
        return Arrays.stream(values())
                .map(department -> new Pair<>(department.getId(), department.name()))
                .collect(Collectors.toList());
    }

    public static Department fromId(int id) {
        return Arrays.stream(values())
                .filter(department -> department.getId() == id)
                .findFirst()
                .orElse(null);
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
