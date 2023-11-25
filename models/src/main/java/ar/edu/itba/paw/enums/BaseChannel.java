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
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public int getId() {
        return ordinal() + 1;
    }
}

