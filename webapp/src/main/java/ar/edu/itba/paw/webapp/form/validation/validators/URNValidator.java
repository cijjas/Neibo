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



    // Compile regex patterns during application startup
    static {
        patternMap.put("channel", Pattern.compile(CHANNEL_URN_REGEX));
        patternMap.put("neighborhood", Pattern.compile(NEIGHBORHOOD_URN_REGEX));
        patternMap.put("amenity", Pattern.compile(AMENITY_URN_REGEX));
        patternMap.put("shifts", Pattern.compile(SHIFT_URN_REGEX));
    }

    // Method to validate a URN based on its type
    public static boolean validateURN(String urn, String type) {
        Pattern pattern = patternMap.get(type);
        return pattern != null && pattern.matcher(urn).matches();
    }
}
