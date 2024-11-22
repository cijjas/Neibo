package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.TwoId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationUtils {
    public static long extractFirstId(String URN) {
        String[] URNParts = URN.split("/");
        if (URNParts.length < 5) { // Check if there are enough parts for an ID
            throw new IllegalArgumentException("Invalid URN format.");
        }
        return Long.parseLong(URNParts[4]);
    }

    public static Long extractOptionalFirstId(String URN) {
        if (URN == null)
            return null;
        String[] URNParts = URN.split("/");
        if (URNParts.length < 5)
            return null;
        return Long.parseLong(URNParts[4]);
    }

    public static TwoId extractTwoId(String URN) {
        String[] URNParts = URN.split("/");
        if (URNParts.length < 7)
            throw new IllegalArgumentException("Invalid URN format.");

        long firstId = Long.parseLong(URNParts[4]);
        long secondId = Long.parseLong(URNParts[6]);

        return new TwoId(firstId, secondId);
    }

    public static Long extractOptionalSecondId(String URN) {
        if (URN == null)
            return null;
        String[] URNParts = URN.split("/");
        if (URNParts.length < 7)
            return null;
        return Long.parseLong(URNParts[6]);
    }

    public static long extractSecondId(String URN){
        String[] URNParts = URN.split("/");
        if (URNParts.length < 7)
            throw new IllegalArgumentException("Invalid URN format.");
        return Long.parseLong(URNParts[6]);
    }

    public static List<Long> extractFirstIds(List<String> URNs){
        List<Long> ids = new ArrayList<>();
        if (URNs == null || URNs.isEmpty())
            return Collections.emptyList();
        for (String URN : URNs)
            ids.add(ValidationUtils.extractFirstId(URN));
        return ids;
    }

    public static List<Long> extractSecondIds(List<String> URNs){
        List<Long> ids = new ArrayList<>();
        if (URNs == null || URNs.isEmpty())
            return Collections.emptyList();
        for (String URN : URNs)
            ids.add(ValidationUtils.extractSecondId(URN));
        return ids;
    }
}
