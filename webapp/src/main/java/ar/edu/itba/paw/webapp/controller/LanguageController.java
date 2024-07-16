package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import ar.edu.itba.paw.webapp.dto.RoleDto;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;


/*
 * # Summary
 *   - A User has a certain Language selected
 *
 * # Use Cases
 *   - A User/Admin/Worker can list the available languages
 *   - A User/Admin/Worker can change his language preference
 *
 * # Embeddable?
 *   - Cant be embedded as it has to be shown at some point, when selecting the language, that would mean to list the Languages
 */


@Path("languages")
@Component
public class LanguageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listLanguages() {
        LOGGER.info("GET request arrived at '/languages'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<LanguageDto> languagesDto = Arrays.stream(Language.values())
                .map(l -> LanguageDto.fromLanguage(l, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<LanguageDto>>(languagesDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findLanguage(
            @PathParam("id") final long languageId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/languages/{}'", languageId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(Long.toString(languageId));
        Response response = checkETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;
        
        // Content
        LanguageDto languageDto = LanguageDto.fromLanguage(Language.fromId(languageId), uriInfo);

        return Response.ok(languageDto)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                .build();
    }
}