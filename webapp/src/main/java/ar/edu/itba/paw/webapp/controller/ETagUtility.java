package ar.edu.itba.paw.webapp.controller;

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

    public static Response checkETagPreconditions(String etag, String entityLevelETag, String rowLevelETag) {
        if (etag != null) {
            if (etag.equals(rowLevelETag) || etag.equals(entityLevelETag)) {
                return Response.status(Response.Status.NOT_MODIFIED)
                        .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                        .header(HttpHeaders.ETAG, entityLevelETag)
                        .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                        .build();
            }
        }
        return null;
    }
}
