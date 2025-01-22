package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.TwoId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExtractionUtils {
    public static long extractFirstId(String URI) {
        String[] URIParts = URI.split("/");
        if (URIParts.length < 5) { // Check if there are enough parts for an ID
            throw new IllegalArgumentException("Invalid URI format.");
        }
        return Long.parseLong(URIParts[4]);
    }

    public static Long extractOptionalFirstId(String URI) {
        if (URI == null)
            return null;
        String[] URIParts = URI.split("/");
        if (URIParts.length < 5)
            return null;
        return Long.parseLong(URIParts[4]);
    }

    public static TwoId extractTwoId(String URI) {
        String[] URIParts = URI.split("/");
        if (URIParts.length < 7)
            throw new IllegalArgumentException("Invalid URI format.");

        long firstId = Long.parseLong(URIParts[4]);
        long secondId = Long.parseLong(URIParts[6]);

        return new TwoId(firstId, secondId);
    }

    public static Long extractOptionalSecondId(String URI) {
        if (URI == null)
            return null;
        String[] URIParts = URI.split("/");
        if (URIParts.length < 7)
            return null;
        return Long.parseLong(URIParts[6]);
    }

    public static long extractSecondId(String URI) {
        String[] URIParts = URI.split("/");
        if (URIParts.length < 7)
            throw new IllegalArgumentException("Invalid URI format.");
        return Long.parseLong(URIParts[6]);
    }

    public static List<Long> extractFirstIds(List<String> URIs) {
        List<Long> ids = new ArrayList<>();
        if (URIs == null || URIs.isEmpty())
            return Collections.emptyList();
        for (String URI : URIs)
            ids.add(ExtractionUtils.extractFirstId(URI));
        return ids;
    }

    public static List<Long> extractSecondIds(List<String> URIs) {
        List<Long> ids = new ArrayList<>();
        if (URIs == null || URIs.isEmpty())
            return Collections.emptyList();
        for (String URI : URIs)
            ids.add(ExtractionUtils.extractSecondId(URI));
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
