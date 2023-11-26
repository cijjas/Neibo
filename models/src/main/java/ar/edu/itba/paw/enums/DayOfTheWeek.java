package ar.edu.itba.paw.enums;

import ar.edu.itba.paw.Pair;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

// DayOfTheWeek.java
public enum DayOfTheWeek {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;

    public static final List<Pair<Integer, String>> DAY_PAIRS = Arrays.stream(values())
            .map(day -> new Pair<>(day.getId(), day.name()))
            .collect(Collectors.toList());

    public static long convertToCustomDayId(int calendarDayId) {
        if (calendarDayId == Calendar.SUNDAY) {
            return DayOfTheWeek.Sunday.getId();
        }

        return calendarDayId - 1;
    }

    public int getId() {
        return ordinal() + 1;
    }
}
