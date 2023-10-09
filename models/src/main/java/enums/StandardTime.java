package enums;


import java.sql.Time;

public enum StandardTime {
    TIME_00_00("00:00:00"),
    TIME_00_30("00:30:00"),
    TIME_01_00("01:00:00"),
    TIME_01_30("01:30:00"),
    TIME_02_00("02:00:00"),
    TIME_02_30("02:30:00"),
    TIME_03_00("03:00:00"),
    TIME_03_30("03:30:00"),
    TIME_04_00("04:00:00"),
    TIME_04_30("04:30:00"),
    TIME_05_00("05:00:00"),
    TIME_05_30("05:30:00"),
    TIME_06_00("06:00:00"),
    TIME_06_30("06:30:00"),
    TIME_07_00("07:00:00"),
    TIME_07_30("07:30:00"),
    TIME_08_00("08:00:00"),
    TIME_08_30("08:30:00"),
    TIME_09_00("09:00:00"),
    TIME_09_30("09:30:00"),
    TIME_10_00("10:00:00"),
    TIME_10_30("10:30:00"),
    TIME_11_00("11:00:00"),
    TIME_11_30("11:30:00"),
    TIME_12_00("12:00:00"),
    TIME_12_30("12:30:00"),
    TIME_13_00("13:00:00"),
    TIME_13_30("13:30:00"),
    TIME_14_00("14:00:00"),
    TIME_14_30("14:30:00"),
    TIME_15_00("15:00:00"),
    TIME_15_30("15:30:00"),
    TIME_16_00("16:00:00"),
    TIME_16_30("16:30:00"),
    TIME_17_00("17:00:00"),
    TIME_17_30("17:30:00"),
    TIME_18_00("18:00:00"),
    TIME_18_30("18:30:00"),
    TIME_19_00("19:00:00"),
    TIME_19_30("19:30:00"),
    TIME_20_00("20:00:00"),
    TIME_20_30("20:30:00"),
    TIME_21_00("21:00:00"),
    TIME_21_30("21:30:00"),
    TIME_22_00("22:00:00"),
    TIME_22_30("22:30:00"),
    TIME_23_00("23:00:00"),
    TIME_23_30("23:30:00");

    private final String value;

    StandardTime(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Time toSqlTime() {
        return Time.valueOf(value);
    }

    public static StandardTime fromSqlTime(Time sqlTime) {
        return StandardTime.valueOf(sqlTime.toString());
    }

    public int getId() {
        return ordinal() + 1;
    }

    @Override
    public String toString() {
        // Remove the last section (seconds) from the time value
        String[] parts = value.split(":");
        if (parts.length >= 2) {
            return parts[0] + ":" + parts[1];
        }
        return value; // Return the original value if splitting fails
    }
}
