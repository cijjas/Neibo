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

@Path("languages")
@Component
public class LanguageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageController.class);

    @Context
    private UriInfo uriInfo;

    private final String storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listLanguages(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                  @Context Request request) {
        LOGGER.info("GET request arrived at '/languages'");
        List<LanguageDto> languagesDto = Arrays.stream(Language.values())
                .map(l -> LanguageDto.fromLanguage(l, uriInfo))
                .collect(Collectors.toList());

        EntityTag entityTag = new EntityTag(storedETag);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");

        return Response.ok(new GenericEntity<List<LanguageDto>>(languagesDto){})
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findLanguage(@PathParam("id") final long id,
                                 @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                 @Context Request request) {
        LOGGER.info("GET request arrived at '/languages/{}'", id);
        LanguageDto languageDto = LanguageDto.fromLanguage(Language.fromId(id), uriInfo);

        EntityTag entityTag = new EntityTag(storedETag);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");
        return Response.ok(languageDto)
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }
}