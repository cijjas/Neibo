package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class URIValidator {
    private static final Map<String, Pattern> patternMap = new HashMap<>();

    private static final String BASE_URL_REGEX = "^(https?://(old-pawserver\\.it\\.itba\\.edu\\.ar/paw-2023b-02|localhost:\\d+/paw-2023b-02))?";

    public static final String CHANNEL_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.CHANNELS + "/\\d+$";
    public static final String NEIGHBORHOOD_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.NEIGHBORHOODS + "/-?\\d+$";
    public static final String AMENITY_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.AMENITIES + "/\\d+$";
    public static final String SHIFT_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.SHIFTS + "/\\d+$";
    public static final String POST_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.POSTS + "/\\d+$";
    public static final String EVENT_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.EVENTS + "/\\d+$";
    public static final String REQUEST_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.PRODUCTS + "/\\d+/" + Endpoint.REQUESTS + "/\\d+$";
    public static final String USER_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.USERS + "/\\d+$";
    public static final String DEPARTMENT_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.DEPARTMENTS + "/\\d+$";
    public static final String WORKER_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.WORKERS + "/\\d+$";
    public static final String INQUIRY_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.PRODUCTS + "/\\d+/" + Endpoint.INQUIRIES + "/\\d+$";
    public static final String PRODUCT_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.PRODUCTS + "/\\d+$";
    public static final String LANGUAGE_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.LANGUAGES + "/\\d+$";
    public static final String REQUEST_STATUS_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.REQUEST_STATUSES + "/\\d+$";
    public static final String PROFESSION_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.PROFESSIONS + "/\\d+$";
    public static final String USER_ROLE_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.USER_ROLES + "/\\d+$";
    public static final String POST_STATUS_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.POST_STATUSES + "/\\d+$";
    public static final String PRODUCT_STATUS_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.PRODUCT_STATUSES + "/\\d+$";
    public static final String WORKER_STATUS_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.WORKER_STATUSES + "/\\d+$";
    public static final String WORKER_ROLE_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.WORKER_ROLES + "/\\d+$";
    public static final String TRANSACTION_TYPE_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.TRANSACTION_TYPES + "/\\d+$";
    public static final String IMAGE_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.IMAGES + "/\\d+$";
    public static final String TAGS_URI_REGEX = BASE_URL_REGEX + "/" + Endpoint.API + "/"  + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.TAGS + "/\\d+$";

    // Compile regex patterns during application startup
    static {
        patternMap.put(Endpoint.CHANNELS, Pattern.compile(CHANNEL_URI_REGEX));
        patternMap.put(Endpoint.NEIGHBORHOODS, Pattern.compile(NEIGHBORHOOD_URI_REGEX));
        patternMap.put(Endpoint.AMENITIES, Pattern.compile(AMENITY_URI_REGEX));
        patternMap.put(Endpoint.SHIFTS, Pattern.compile(SHIFT_URI_REGEX));
        patternMap.put(Endpoint.POSTS, Pattern.compile(POST_URI_REGEX));
        patternMap.put(Endpoint.EVENTS, Pattern.compile(EVENT_URI_REGEX));
        patternMap.put(Endpoint.REQUESTS, Pattern.compile(REQUEST_URI_REGEX));
        patternMap.put(Endpoint.USERS, Pattern.compile(USER_URI_REGEX));
        patternMap.put(Endpoint.DEPARTMENTS, Pattern.compile(DEPARTMENT_URI_REGEX));
        patternMap.put(Endpoint.INQUIRIES, Pattern.compile(INQUIRY_URI_REGEX));
        patternMap.put(Endpoint.WORKERS, Pattern.compile(WORKER_URI_REGEX));
        patternMap.put(Endpoint.LANGUAGES, Pattern.compile(LANGUAGE_URI_REGEX));
        patternMap.put(Endpoint.PROFESSIONS, Pattern.compile(PROFESSION_URI_REGEX));
        patternMap.put(Endpoint.USER_ROLES, Pattern.compile(USER_ROLE_URI_REGEX));
        patternMap.put(Endpoint.PRODUCTS, Pattern.compile(PRODUCT_URI_REGEX));
        patternMap.put(Endpoint.IMAGES, Pattern.compile(IMAGE_URI_REGEX));
        patternMap.put(Endpoint.WORKER_ROLES, Pattern.compile(WORKER_ROLE_URI_REGEX));
        patternMap.put(Endpoint.REQUEST_STATUSES, Pattern.compile(REQUEST_STATUS_URI_REGEX));
        patternMap.put(Endpoint.TAGS, Pattern.compile(TAGS_URI_REGEX));
        patternMap.put(Endpoint.POST_STATUSES, Pattern.compile(POST_STATUS_URI_REGEX));
        patternMap.put(Endpoint.PRODUCT_STATUSES, Pattern.compile(PRODUCT_STATUS_URI_REGEX));
        patternMap.put(Endpoint.TRANSACTION_TYPES, Pattern.compile(TRANSACTION_TYPE_URI_REGEX));
        patternMap.put(Endpoint.WORKER_STATUSES, Pattern.compile(WORKER_STATUS_URI_REGEX));
    }

    // Method to validate a URI based on its type
    public static boolean validateOptionalURI(String URI, String type) {
        return URI == null || validateURI(URI, type);
    }

    public static boolean validateURI(String URI, String type) {
        Pattern pattern = patternMap.get(type);
        return pattern != null && URI != null && !URI.isEmpty() && pattern.matcher(URI).matches();
    }
}

