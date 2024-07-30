package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.interfaces.services.ProfessionService;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.webapp.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

/*
 * # Summary
 *   - A Worker can have many Professions
 *
 * # Use cases
 *   - The Workers can choose which Professions they have
 *   - A User/Admin can filter the Workers through certain Profession
 *
 */

@Path("professions")
@Component
public class ProfessionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionController.class);

    @Autowired
    private ProfessionService ps;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    // falta un preauthorize aca para que no usen el query param
    public Response listProfessions(
            @QueryParam("forWorker") final String workerURN
    ) {
        LOGGER.info("GET request arrived at '/professions'");

        // Content
        List<Profession> professions = ps.getProfessions(workerURN);
        String professionsHashCode = String.valueOf(professions.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(professionsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (professions.isEmpty())
            return Response.noContent()
                    .tag(professionsHashCode)
                    .build();

        List<ProfessionDto> professionDto = professions.stream()
                .map(p -> ProfessionDto.fromProfession(Professions.valueOf(p.getProfession().toString()), uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ProfessionDto>>(professionDto){})
                .cacheControl(cacheControl)
                .tag(professionsHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findProfession(
            @PathParam("id") final long id
    ) {
        LOGGER.info("GET request arrived at '/professions/{}'", id);

        // Content
        Professions profession = Professions.fromId(id);
        String professionHashCode = String.valueOf(profession.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(professionHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ProfessionDto.fromProfession(profession, uriInfo))
                .cacheControl(cacheControl)
                .tag(professionHashCode)
                .build();
    }
}
