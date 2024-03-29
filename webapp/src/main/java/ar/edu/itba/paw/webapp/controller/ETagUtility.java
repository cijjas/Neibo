package ar.edu.itba.paw.webapp.controller;

import javax.ws.rs.core.EntityTag;
import java.time.Instant;
import java.util.Date;

public class ETagUtility {

    public static EntityTag generateETag() {
        return new EntityTag(String.valueOf(System.currentTimeMillis()));
    }
}
