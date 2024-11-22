package ar.edu.itba.paw.webapp.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class URNValidator {
    private static final Map<String, Pattern> patternMap = new HashMap<>();

    private static final String CHANNEL_URN_REGEX = "^(https?://[^/]+)?/neighborhoods/\\d+/channels/\\d+$";
    private static final String NEIGHBORHOOD_URN_REGEX = "^(https?://[^/]+)?/neighborhoods/\\d+$";
    private static final String AMENITY_URN_REGEX = "^(https?://[^/]+)?/neighborhoods/\\d+/amenities/\\d+$";
    private static final String SHIFT_URN_REGEX = "^(https?://[^/]+)?/shifts/\\d+$";
    private static final String POST_URN_REGEX = "^(https?://[^/]+)?/neighborhoods/\\d+/posts/\\d+$";
    private static final String REQUEST_URN_REGEX = "^(https?://[^/]+)?/neighborhoods/\\d+/products/\\d+/requests/\\d+$";
    private static final String USER_URN_REGEX = "^(https?://[^/]+)?/neighborhoods/\\d+/users/\\d+$";
    private static final String DEPARTMENT_URN_REGEX = "^(https?://[^/]+)?/departments/\\d+$";
    private static final String WORKERS_URN_REGEX = "^(https?://[^/]+)?/workers/\\d+$";
    private static final String INQUIRY_URN_REGEX = "^(https?://[^/]+)?/neighborhoods/\\d+/products/\\d+/inquiries/\\d+$";
    private static final String PRODUCT_URN_REGEX = "^(https?://[^/]+)?/neighborhoods/\\d+/products/\\d+$";
    private static final String LANGUAGE_URN_REGEX = "^(https?://[^/]+)?/languages/\\d+$";
    private static final String REQUEST_STATUS_URN_REGEX = "^(https?://[^/]+)?/request-statuses/\\d+$";
    private static final String PROFESSION_URN_REGEX = "^(https?://[^/]+)?/professions/\\d+$";
    private static final String USER_ROLE_URN_REGEX = "^(https?://[^/]+)?/user-roles/\\d+$";
    private static final String POST_STATUS_URN_REGEX = "^(https?://[^/]+)?/post-statuses/\\d+$";
    private static final String PRODUCT_STATUS_URN_REGEX = "^(https?://[^/]+)?/product-statuses/\\d+$";
    private static final String WORKER_STATUS_URN_REGEX = "^(https?://[^/]+)?/worker-statuses/\\d+$";
    private static final String WORKER_ROLE_URN_REGEX = "^(https?://[^/]+)?/worker-roles/\\d+$";
    private static final String TRANSACTION_TYPE_URN_REGEX = "^(https?://[^/]+)?/transaction-types/\\d+$";
    private static final String IMAGE_URN_REGEX = "^(https?://[^/]+)?/images/\\d+$";
    private static final String TAGS_URN_REGEX = "^(https?://[^/]+)?/neighborhoods/\\d+/tags/\\d+$";

    // Compile regex patterns during application startup
    static {
        patternMap.put("channel", Pattern.compile(CHANNEL_URN_REGEX));
        patternMap.put("neighborhood", Pattern.compile(NEIGHBORHOOD_URN_REGEX));
        patternMap.put("amenity", Pattern.compile(AMENITY_URN_REGEX));
        patternMap.put("shifts", Pattern.compile(SHIFT_URN_REGEX));
        patternMap.put("posts", Pattern.compile(POST_URN_REGEX));
        patternMap.put("requests", Pattern.compile(REQUEST_URN_REGEX));
        patternMap.put("users", Pattern.compile(USER_URN_REGEX));
        patternMap.put("departments", Pattern.compile(DEPARTMENT_URN_REGEX));
        patternMap.put("inquiries", Pattern.compile(INQUIRY_URN_REGEX));
        patternMap.put("workers", Pattern.compile(WORKERS_URN_REGEX));
        patternMap.put("language", Pattern.compile(LANGUAGE_URN_REGEX));
        patternMap.put("professions", Pattern.compile(PROFESSION_URN_REGEX));
        patternMap.put("userRole", Pattern.compile(USER_ROLE_URN_REGEX));
        patternMap.put("product", Pattern.compile(PRODUCT_URN_REGEX));
        patternMap.put("images", Pattern.compile(IMAGE_URN_REGEX));
        patternMap.put("workerRole", Pattern.compile(WORKER_ROLE_URN_REGEX));
        patternMap.put("requestStatus", Pattern.compile(REQUEST_STATUS_URN_REGEX));
        patternMap.put("tags", Pattern.compile(TAGS_URN_REGEX));
        patternMap.put("post-status", Pattern.compile(POST_STATUS_URN_REGEX));
        patternMap.put("product-status", Pattern.compile(PRODUCT_STATUS_URN_REGEX));
        patternMap.put("transaction-type", Pattern.compile(TRANSACTION_TYPE_URN_REGEX));
        patternMap.put("worker-status", Pattern.compile(WORKER_STATUS_URN_REGEX));
    }

    // Method to validate a URN based on its type
    public static boolean validateURN(String urn, String type) {
        Pattern pattern = patternMap.get(type);
        return urn == null || (pattern != null && pattern.matcher(urn).matches());
    }
}
