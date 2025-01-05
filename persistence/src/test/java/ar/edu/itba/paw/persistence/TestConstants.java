package ar.edu.itba.paw.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestConstants {
    public static final int BASE_PAGE = 1;
    public static final int BASE_PAGE_SIZE = 20;
    public static final int TEST_PAGE = 2;
    public static final int TEST_PAGE_SIZE = 1;

    public static final int INVALID_ID = -1;
    public static final long INVALID_LONG_ID = -1L;
    public static final String INVALID_STRING_ID = "INVALID";

    public static final String USER_MAIL_1 = "usermail1@test.com";
    public static final String USER_MAIL_2 = "usermail2@test.com";
    public static final String USER_MAIL_3 = "usermail3@test.com";
    public static final String USER_MAIL_4 = "usermail4@test.com";
    public static final String USER_MAIL_5 = "usermail5@test.com";
    public static final String USER_MAIL_6 = "usermail6@test.com";
    public static final String USER_MAIL_7 = "usermail7@test.com";
    public static final String WORKER_MAIL_1 = "workermail1@test.com";
    public static final String WORKER_MAIL_2 = "workermail2@test.com";
    public static final String WORKER_MAIL_3 = "workermail3@test.com";
    public static final String WORKER_MAIL_4 = "workermail4@test.com";

    public static final String NEIGHBORHOOD_NAME_1 = "Neighborhood 1";
    public static final String NEIGHBORHOOD_NAME_2 = "Neighborhood 2";
    public static final String NEIGHBORHOOD_NAME_3 = "Neighborhood 3";
    public static final String NEIGHBORHOOD_NAME_4 = "Neighborhood 4";

    public static final String PROFESSION_NAME_1 = "Plumber";
    public static final String PROFESSION_NAME_2 = "Thief";

    public static final String DEPARTMENT_NAME_1 = "Best department";
    public static final String DEPARTMENT_NAME_2 = "Not the Best department";
    public static final String DEPARTMENT_NAME_3 = "Maybe the worst department";

    public static final int NO_ELEMENTS = 0;
    public static final int ONE_ELEMENT = 1;
    public static final int TWO_ELEMENTS = 2;
    public static final int THREE_ELEMENTS = 3;
    public static final int FOUR_ELEMENTS = 4;
    public static final int FIVE_ELEMENTS = 5;
    public static final int SIX_ELEMENTS = 6;
    public static final int SEVEN_ELEMENTS = 7;
    public static final int EIGHT_ELEMENTS = 8;
    public static final int NINE_ELEMENTS = 9;
    public static final int TEN_ELEMENTS = 10;
    public static final int ELEVEN_ELEMENTS = 11;
    public static final int TWELVE_ELEMENTS = 12;
    public static final int THIRTEEN_ELEMENTS = 13;
    public static final int NINETEEN_ELEMENTS = 19;

    public static final Long EMPTY_FIELD = null;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    public static Date DATE_1;
    public static Date DATE_2;
    public static Date DATE_3;
    public static Date DATE_4;

    static {
        try {
            DATE_1 = formatter.parse("2024-09-09");
            DATE_2 = formatter.parse("2024-09-10");
            DATE_3 = formatter.parse("2024-09-11");
            DATE_4 = formatter.parse("2024-09-11");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Date NO_DATE = null;
    public static final Integer NO_DAY_ID = null;

}
