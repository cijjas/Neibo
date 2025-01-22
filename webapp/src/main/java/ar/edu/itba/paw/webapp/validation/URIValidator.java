package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class URIValidator {
    private static final Map<String, Pattern> patternMap = new HashMap<>();

    private static final String CHANNEL_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.CHANNELS + "/\\d+$";
    private static final String NEIGHBORHOOD_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/-?\\d+$";
    private static final String AMENITY_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.AMENITIES + "/\\d+$";
    private static final String SHIFT_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.SHIFTS + "/\\d+$";
    private static final String POST_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.POSTS + "/\\d+$";
    private static final String EVENT_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.EVENTS + "/\\d+$";
    private static final String REQUEST_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.PRODUCTS + "/\\d+/" + Endpoint.REQUESTS + "/\\d+$";
    private static final String USER_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.USERS + "/\\d+$";
    private static final String DEPARTMENT_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.DEPARTMENTS + "/\\d+$";
    private static final String WORKERS_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.WORKERS + "/\\d+$";
    private static final String INQUIRY_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.PRODUCTS + "/\\d+/" + Endpoint.INQUIRIES + "/\\d+$";
    private static final String PRODUCT_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.PRODUCTS + "/\\d+$";
    private static final String LANGUAGE_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.LANGUAGES + "/\\d+$";
    private static final String REQUEST_STATUS_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.REQUEST_STATUSES + "/\\d+$";
    private static final String PROFESSION_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.PROFESSIONS + "/\\d+$";
    private static final String USER_ROLE_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.USER_ROLES + "/\\d+$";
    private static final String POST_STATUS_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.POST_STATUSES + "/\\d+$";
    private static final String PRODUCT_STATUS_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.PRODUCT_STATUSES + "/\\d+$";
    private static final String WORKER_STATUS_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.WORKER_STATUSES + "/\\d+$";
    private static final String WORKER_ROLE_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.WORKER_ROLES + "/\\d+$";
    private static final String TRANSACTION_TYPE_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.TRANSACTION_TYPES + "/\\d+$";
    private static final String IMAGE_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.IMAGES + "/\\d+$";
    private static final String TAGS_URI_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.TAGS + "/\\d+$";

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
        patternMap.put(Endpoint.WORKERS, Pattern.compile(WORKERS_URI_REGEX));
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
    public static boolean validateURI(String URI, String type) {
        Pattern pattern = patternMap.get(type);
        return URI == null || (pattern != null && pattern.matcher(URI).matches());
    }
}

