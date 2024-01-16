package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.Pair;

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

    public static DayOfTheWeek fromId(int id) {
        return Arrays.stream(values())
                .filter(day -> day.getId() == id)
                .findFirst()
                .orElse(null);
    }

}
