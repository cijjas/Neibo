package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.interfaces.services.ProfessionWorkerService;
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

@Path("professions")
@Component
public class ProfessionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionController.class);

    @Autowired
    private ProfessionWorkerService ps;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProfessions(
            @QueryParam("workerId") final Long workerId
    ) {
        LOGGER.info("GET request arrived at '/professions'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<Profession> professions = ps.getWorkerProfessions(workerId);
        if (professions.isEmpty())
            return Response.noContent().build();
        List<ProfessionDto> professionDto = professions.stream()
                .map(p -> ProfessionDto.fromProfession(p, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ProfessionDto>>(professionDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findProfession(
            @PathParam("id") final long id
    ) {
        LOGGER.info("GET request arrived at '/professions/{}'", id);
        ProfessionDto professionDto = ProfessionDto.fromProfession(Professions.fromId(id), uriInfo);

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");
        return Response.ok(professionDto)
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }
}
