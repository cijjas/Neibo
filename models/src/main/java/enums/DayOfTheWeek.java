package enums;

// DayOfTheWeek.java
public enum DayOfTheWeek {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;

    public int getId() {
        return ordinal() + 1;
    }
}
