package enums;

public enum Professions {
    PLUMBER,
    ELECTRICIAN,
    POOL_MAINTENANCE,
    GARDENER,
    CARPENTER;

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public int getId() {
        return ordinal() + 1;
    }
}
