package ar.edu.itba.paw.enums;

public enum BaseChannel {
    ANNOUNCEMENTS,
    COMPLAINTS,
    FEED,
    WORKERS,

    RESERVATIONS,

    INFORMATION;

    @Override
    public String toString() {
        // Convert the enum name to lowercase
        String name = name().toLowerCase();

        // Capitalize the first letter
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public int getId() {
        return ordinal() + 1;
    }
}

