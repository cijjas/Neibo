package ar.edu.itba.paw.webapp.controller;

import java.time.Instant;
import java.util.Date;

public class ETagUtility {

    public static String generateETag() {
        // Use the current timestamp as the ETag
        return String.valueOf(System.currentTimeMillis());
    }

    public static Date getCurrentTimestampForPreconditions() {
        // Get the current timestamp as a Date
        return Date.from(Instant.now());
    }

}
