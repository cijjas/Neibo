package ar.edu.itba.paw.enums;

public enum Month {
    JANUARY("January", "enero"),
    FEBRUARY("February", "febrero"),
    MARCH("March", "marzo"),
    APRIL("April", "abril"),
    MAY("May", "mayo"),
    JUNE("June", "junio"),
    JULY("July", "julio"),
    AUGUST("August", "agosto"),
    SEPTEMBER("September", "septiembre"),
    OCTOBER("October", "octubre"),
    NOVEMBER("November", "noviembre"),
    DECEMBER("December", "diciembre");

    private final String englishName;
    private final String spanishName;

    Month(String englishName, String spanishName) {
        this.englishName = englishName;
        this.spanishName = spanishName;
    }

    public String getName(Language language) {
        return language == Language.ENGLISH ? englishName : spanishName;
    }
}

