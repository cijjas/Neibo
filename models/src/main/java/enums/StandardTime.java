package enums;


import java.sql.Time;

public enum StandardTime {
    TIME_00_00("00:00:00"),
    TIME_01_00("01:00:00"),
    TIME_02_00("02:00:00"),
    TIME_03_00("03:00:00"),
    TIME_04_00("04:00:00"),
    TIME_05_00("05:00:00"),
    TIME_06_00("06:00:00"),
    TIME_07_00("07:00:00"),
    TIME_08_00("08:00:00"),
    TIME_09_00("09:00:00"),
    TIME_10_00("10:00:00"),
    TIME_11_00("11:00:00"),
    TIME_12_00("12:00:00"),
    TIME_13_00("13:00:00"),
    TIME_14_00("14:00:00"),
    TIME_15_00("15:00:00"),
    TIME_16_00("16:00:00"),
    TIME_17_00("17:00:00"),
    TIME_18_00("18:00:00"),
    TIME_19_00("19:00:00"),
    TIME_20_00("20:00:00"),
    TIME_21_00("21:00:00"),
    TIME_22_00("22:00:00"),
    TIME_23_00("23:00:00");

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
