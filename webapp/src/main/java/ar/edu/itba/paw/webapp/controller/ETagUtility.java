package ar.edu.itba.paw.webapp.controller;

import javax.swing.text.html.parser.Entity;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import java.time.Instant;
import java.util.Date;

import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.CUSTOM_ROW_LEVEL_ETAG_NAME;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.MAX_AGE_HEADER;

public class ETagUtility {

    public static EntityTag generateETag() {
        return new EntityTag(Long.toString(System.currentTimeMillis()));
    }

    // List y Find
    public static Response checkETagPreconditions(EntityTag clientEtag, EntityTag entityLevelETag, EntityTag rowLevelETag) {
        if (clientEtag != null) {
            if (clientEtag.equals(rowLevelETag) || clientEtag.equals(entityLevelETag)) {
                return Response.status(Response.Status.NOT_MODIFIED)
                        .tag(entityLevelETag)
                        .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                        .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                        .build();
            }
        }
        return null;
    }

    public static Response checkMutableETagPreconditions(EntityTag clientEtag, EntityTag entityLevelETag, EntityTag rowLevelETag) {
        if (clientEtag != null) {
            if (clientEtag.equals(rowLevelETag) || clientEtag.equals(entityLevelETag)) {
                return Response.status(Response.Status.NOT_MODIFIED)
                        .tag(entityLevelETag)
                        .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                        .build();
            }
        }
        return null;
    }

    // Post puede usar directamente cache control

    // Update y delete
    public static Response checkModificationETagPreconditions(EntityTag clientEtag, EntityTag entityLevelETag, EntityTag rowLevelETag) {
        if (clientEtag != null) {
            if (!clientEtag.equals(rowLevelETag) && !clientEtag.equals(entityLevelETag)) {
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .tag(entityLevelETag)
                        .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                        .build();
            }
        }
        return null;
    }
}
