package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class URNValidator {
    private static final Map<String, Pattern> patternMap = new HashMap<>();

    private static final String CHANNEL_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.CHANNELS + "/\\d+$";
    private static final String NEIGHBORHOOD_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/-?\\d+$";
    private static final String AMENITY_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.AMENITIES + "/\\d+$";
    private static final String SHIFT_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.SHIFTS + "/\\d+$";
    private static final String POST_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.POSTS + "/\\d+$";
    private static final String EVENT_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.EVENTS + "/\\d+$";
    private static final String REQUEST_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.PRODUCTS + "/\\d+/" + Endpoint.REQUESTS + "/\\d+$";
    private static final String USER_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.USERS + "/\\d+$";
    private static final String DEPARTMENT_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.DEPARTMENTS + "/\\d+$";
    private static final String WORKERS_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.WORKERS + "/\\d+$";
    private static final String INQUIRY_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.PRODUCTS + "/\\d+/" + Endpoint.INQUIRIES + "/\\d+$";
    private static final String PRODUCT_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.PRODUCTS + "/\\d+$";
    private static final String LANGUAGE_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.LANGUAGES + "/\\d+$";
    private static final String REQUEST_STATUS_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.REQUEST_STATUSES + "/\\d+$";
    private static final String PROFESSION_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.PROFESSIONS + "/\\d+$";
    private static final String USER_ROLE_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.USER_ROLES + "/\\d+$";
    private static final String POST_STATUS_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.POST_STATUSES + "/\\d+$";
    private static final String PRODUCT_STATUS_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.PRODUCT_STATUSES + "/\\d+$";
    private static final String WORKER_STATUS_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.WORKER_STATUSES + "/\\d+$";
    private static final String WORKER_ROLE_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.WORKER_ROLES + "/\\d+$";
    private static final String TRANSACTION_TYPE_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.TRANSACTION_TYPES + "/\\d+$";
    private static final String IMAGE_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.IMAGES + "/\\d+$";
    private static final String TAGS_URN_REGEX = "^(https?://[^/]+)?/" + Endpoint.NEIGHBORHOODS + "/\\d+/" + Endpoint.TAGS + "/\\d+$";

    // Compile regex patterns during application startup
    static {
        patternMap.put(Endpoint.CHANNELS, Pattern.compile(CHANNEL_URN_REGEX));
        patternMap.put(Endpoint.NEIGHBORHOODS, Pattern.compile(NEIGHBORHOOD_URN_REGEX));
        patternMap.put(Endpoint.AMENITIES, Pattern.compile(AMENITY_URN_REGEX));
        patternMap.put(Endpoint.SHIFTS, Pattern.compile(SHIFT_URN_REGEX));
        patternMap.put(Endpoint.POSTS, Pattern.compile(POST_URN_REGEX));
        patternMap.put(Endpoint.EVENTS, Pattern.compile(EVENT_URN_REGEX));
        patternMap.put(Endpoint.REQUESTS, Pattern.compile(REQUEST_URN_REGEX));
        patternMap.put(Endpoint.USERS, Pattern.compile(USER_URN_REGEX));
        patternMap.put(Endpoint.DEPARTMENTS, Pattern.compile(DEPARTMENT_URN_REGEX));
        patternMap.put(Endpoint.INQUIRIES, Pattern.compile(INQUIRY_URN_REGEX));
        patternMap.put(Endpoint.WORKERS, Pattern.compile(WORKERS_URN_REGEX));
        patternMap.put(Endpoint.LANGUAGES, Pattern.compile(LANGUAGE_URN_REGEX));
        patternMap.put(Endpoint.PROFESSIONS, Pattern.compile(PROFESSION_URN_REGEX));
        patternMap.put(Endpoint.USER_ROLES, Pattern.compile(USER_ROLE_URN_REGEX));
        patternMap.put(Endpoint.PRODUCTS, Pattern.compile(PRODUCT_URN_REGEX));
        patternMap.put(Endpoint.IMAGES, Pattern.compile(IMAGE_URN_REGEX));
        patternMap.put(Endpoint.WORKER_ROLES, Pattern.compile(WORKER_ROLE_URN_REGEX));
        patternMap.put(Endpoint.REQUEST_STATUSES, Pattern.compile(REQUEST_STATUS_URN_REGEX));
        patternMap.put(Endpoint.TAGS, Pattern.compile(TAGS_URN_REGEX));
        patternMap.put(Endpoint.POST_STATUSES, Pattern.compile(POST_STATUS_URN_REGEX));
        patternMap.put(Endpoint.PRODUCT_STATUSES, Pattern.compile(PRODUCT_STATUS_URN_REGEX));
        patternMap.put(Endpoint.TRANSACTION_TYPES, Pattern.compile(TRANSACTION_TYPE_URN_REGEX));
        patternMap.put(Endpoint.WORKER_STATUSES, Pattern.compile(WORKER_STATUS_URN_REGEX));
    }

    // Method to validate a URN based on its type
    public static boolean validateURN(String urn, String type) {
        Pattern pattern = patternMap.get(type);
        return urn == null || (pattern != null && pattern.matcher(urn).matches());
    }
}

