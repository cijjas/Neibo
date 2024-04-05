package ar.edu.itba.paw.webapp.form.validation.validators;

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
    private static final String PROFESSION_URN_REGEX = "^(https?://[^/]+)?/professions/\\d+$";
    private static final String USER_ROLE_URN_REGEX = "^(https?://[^/]+)?/user-roles/\\d+$";

    // Compile regex patterns during application startup<
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
    }

    // Method to validate a URN based on its type
    public static boolean validateURN(String urn, String type) {
        Pattern pattern = patternMap.get(type);
        return pattern != null && pattern.matcher(urn).matches();
    }
}
