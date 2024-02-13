package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.Pair;
import ar.edu.itba.paw.exceptions.NotFoundException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

// DayOfTheWeek.java
public enum DayOfTheWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static final List<Pair<Integer, String>> DAY_PAIRS = Arrays.stream(values())
            .map(day -> new Pair<>(day.getId(), day.name()))
            .collect(Collectors.toList());

    public static long convertToCustomDayId(int calendarDayId) {
        if (calendarDayId == Calendar.SUNDAY) {
            return DayOfTheWeek.SUNDAY.getId();
        }

        return calendarDayId - 1;
    }

    public int getId() {
        return ordinal() + 1;
    }

    public static DayOfTheWeek fromId(long id) {
        if(id <= 0)
            throw new IllegalArgumentException("Invalid value (" + id + ") for the Day ID. Please use a positive integer greater than 0.");
        return Arrays.stream(values())
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Day Not Found"));
    }

}
