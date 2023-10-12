package enums;

public enum Professions {
    PLOMERO,
    ELECTRICISTA,
    PILETERO,
    JARDINERO,
    CARPINTERO;

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public int getId() {
        return ordinal() + 1;
    }
}
