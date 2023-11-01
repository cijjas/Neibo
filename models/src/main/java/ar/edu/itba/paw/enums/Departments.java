package ar.edu.itba.paw.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Departments {
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
    ELECTRONICS_ACCESSORIES;


    public static final List<Pair<Integer, String>> DEPARTMENT_PAIRS = Arrays.stream(values())
            .map(department -> new Pair<>(department.getId(), department.toString()))
            .collect(Collectors.toList());

    public static List<String> getDepartmentsList() {
        return Arrays.stream(values())
                .map(department -> department.toString().toLowerCase())
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
