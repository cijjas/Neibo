package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.TwoId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExtractionUtils {
    private static final int BASE_PATH_INDEX = 5;

    public static long extractFirstId(String URI) {
        String[] URIParts = URI.split("/");
        if (URIParts.length <= BASE_PATH_INDEX) {
            throw new IllegalArgumentException("Invalid URI format.");
        }
        return Long.parseLong(URIParts[BASE_PATH_INDEX]);
    }

    public static Long extractOptionalFirstId(String URI) {
        if (URI == null) return null;
        String[] URIParts = URI.split("/");
        if (URIParts.length <= BASE_PATH_INDEX) return null;
        return Long.parseLong(URIParts[BASE_PATH_INDEX]);
    }

    public static TwoId extractTwoId(String URI) {
        String[] URIParts = URI.split("/");
        if (URIParts.length <= BASE_PATH_INDEX + 2) {
            throw new IllegalArgumentException("Invalid URI format.");
        }

        long firstId = Long.parseLong(URIParts[BASE_PATH_INDEX]);
        long secondId = Long.parseLong(URIParts[BASE_PATH_INDEX + 2]);

        return new TwoId(firstId, secondId);
    }

    public static Long extractOptionalSecondId(String URI) {
        if (URI == null) return null;
        String[] URIParts = URI.split("/");
        if (URIParts.length <= BASE_PATH_INDEX + 2) return null;
        return Long.parseLong(URIParts[BASE_PATH_INDEX + 2]);
    }

    public static long extractSecondId(String URI) {
        String[] URIParts = URI.split("/");
        if (URIParts.length <= BASE_PATH_INDEX + 2) {
            throw new IllegalArgumentException("Invalid URI format.");
        }
        return Long.parseLong(URIParts[BASE_PATH_INDEX + 2]);
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

    public static Date extractDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid Date");
        }
    }

    public static Date extractOptionalDate(String date) {
        if (date == null)
            return null;
        return extractDate(date);
    }
}
