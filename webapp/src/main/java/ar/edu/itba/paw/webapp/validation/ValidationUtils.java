package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.ThreeId;
import ar.edu.itba.paw.models.TwoId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ValidationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtils.class);

    public static long extractId(String URN) {
        String[] URNParts = URN.split("/");
        if (URNParts.length < 5) { // Check if there are enough parts for an ID
            throw new IllegalArgumentException("Invalid URN format.");
        }
        return Long.parseLong(URNParts[4]);
    }

    public static long extractFirstId(String URN){
        return extractId(URN);
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

    public static long extractSecondId(String URN){
        String[] URNParts = URN.split("/");
        if (URNParts.length < 7)
            throw new IllegalArgumentException("Invalid URN format.");
        return Long.parseLong(URNParts[6]);
    }

    public static TwoId extractOptionalTwoId(String URN) {
        if (URN == null)
            return null;
        return extractTwoId(URN);
    }

    public static ThreeId extractThreeId(String URN) {
        String[] URNParts = URN.split("/");
        if (URNParts.length < 9)
            throw new IllegalArgumentException("Invalid URN format.");

        long firstId = Long.parseLong(URNParts[4]);
        long secondId = Long.parseLong(URNParts[6]);
        long thirdId = Long.parseLong(URNParts[8]);

        return new ThreeId(firstId, secondId, thirdId);
    }

    public static long extractThirdId(String URN){
        String[] URNParts = URN.split("/");
        if (URNParts.length < 9)
            throw new IllegalArgumentException("Invalid URN format.");
        return Long.parseLong(URNParts[8]);
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
