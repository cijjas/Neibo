package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.constants.Constant.IMMUTABLE;
import static ar.edu.itba.paw.webapp.controller.constants.Constant.MAX_AGE_SECONDS;

/*
 * # Summary
 *   - A User has a certain Language selected
 *
 * # Use Cases
 *   - Anyone can list the available languages
 *   - A Registered User can change his language preference
 */

@Path(Endpoint.API + "/" + Endpoint.LANGUAGES)
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class LanguageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    public Response listLanguages() {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        Language[] languages = Language.values();
        String languagesHashCode = String.valueOf(Arrays.hashCode(languages));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(languagesHashCode));
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<LanguageDto> languagesDto = Arrays.stream(Language.values())
                .map(l -> LanguageDto.fromLanguage(l, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<LanguageDto>>(languagesDto) {
                })
                .cacheControl(cacheControl)
                .tag(languagesHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.LANGUAGE_ID + "}")
    public Response findLanguage(
            @PathParam(PathParameter.LANGUAGE_ID) long languageId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Language language = Language.fromId(languageId).orElseThrow(NotFoundException::new);
        String languageHashCode = String.valueOf(language.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(languageHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(LanguageDto.fromLanguage(language, uriInfo))
                .cacheControl(cacheControl)
                .tag(languageHashCode)
                .build();
    }
}