package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExtractionUtils {


    private static String getBasePath(String fullUri) {
        if (fullUri == null || fullUri.isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null or empty");
        }

        URI uri = URI.create(fullUri);
        String path = uri.getPath();

        int apiIndex = path.indexOf("/" + Endpoint.API + "/");
        if (apiIndex == -1) {
            throw new IllegalArgumentException("Invalid API path structure");
        }

        return apiIndex == 0 ? "" : path.substring(0, apiIndex);
    }

    /**
     * Validates that the URI follows the correct structure: basePath/api/...
     * This ensures no unexpected segments appear before the api path.
     */
    private static boolean isValidApiPath(String fullUri) {
        if (fullUri == null || fullUri.isEmpty()) {
            return false;
        }

        URI uri = URI.create(fullUri);
        String path = uri.getPath();

        // Get the base path
        String basePath = getBasePath(fullUri);

        // Check if the path starts with basePath/api/
        String expectedStart = basePath + "/" + Endpoint.API + "/";
        return path.startsWith(expectedStart);
    }

    /**
     * Extracts the relative path starting from the API segment, but only if the overall path structure is valid
     */
    private static String[] getRelativePathSegments(String fullUri) {
        if (!isValidApiPath(fullUri)) {
            throw new IllegalArgumentException("Invalid API path structure: " + fullUri);
        }

        URI uri = URI.create(fullUri);
        String path = uri.getPath();

        // Get the base path
        String basePath = getBasePath(fullUri);

        // Remove the base path and split the remaining path
        String apiPath = path.substring(basePath.length());
        String[] segments = apiPath.split("/");

        // Filter out empty segments
        return Arrays.stream(segments)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    public static boolean isNeighborhoodPath(String uri) {
        try {
            String[] segments = getRelativePathSegments(uri);
            return segments.length >= 2 &&
                    Endpoint.API.equals(segments[0]) &&
                    Endpoint.NEIGHBORHOODS.equals(segments[1]);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static long extractNeighborhoodId(String uri) {
        String[] segments = getRelativePathSegments(uri);
        if (segments.length < 3 || !Endpoint.NEIGHBORHOODS.equals(segments[1])) {
            throw new IllegalArgumentException("URI does not contain valid neighborhood ID");
        }
        try {
            return Long.parseLong(segments[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid neighborhood ID format", e);
        }
    }

    public static Long extractFirstId(String uri) {
        String[] segments = getRelativePathSegments(uri);
        if (segments.length < 3) {
            throw new IllegalArgumentException("URI does not contain first ID");
        }
        return Long.parseLong(segments[2]);
    }

    public static Long extractOptionalFirstId(String uri) {
        try {
            return extractFirstId(uri);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long extractSecondId(String uri) {
        String[] segments = getRelativePathSegments(uri);
        if (segments.length < 5) {
            throw new IllegalArgumentException("URI does not contain second ID");
        }
        return Long.parseLong(segments[4]);
    }

    public static List<Long> extractFirstIds(List<String> URIs) {
        if (URIs == null || URIs.isEmpty()) return Collections.emptyList();
        List<Long> ids = new ArrayList<>();
        for (String URI : URIs) {
            ids.add(ExtractionUtils.extractFirstId(URI));
        }
        return ids;
    }

    public static List<Long> extractSecondIds(List<String> URIs) {
        if (URIs == null || URIs.isEmpty()) return Collections.emptyList();
        List<Long> ids = new ArrayList<>();
        for (String URI : URIs) {
            ids.add(ExtractionUtils.extractSecondId(URI));
        }
        return ids;
    }

    public static TwoId extractTwoId(String uri) {
        Long firstId = extractFirstId(uri);
        Long secondId = extractSecondId(uri);
        return new TwoId(firstId, secondId);
    }

    public static Date extractDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid Date");
        }
    }

    public static Long extractOptionalSecondId(String uri) {
        try {
            return extractSecondId(uri);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date extractOptionalDate(String date) {
        if (date == null)
            return null;
        return extractDate(date);
    }
}
