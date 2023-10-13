package enums;

import java.util.Calendar;

// DayOfTheWeek.java
public enum DayOfTheWeek {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;

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
