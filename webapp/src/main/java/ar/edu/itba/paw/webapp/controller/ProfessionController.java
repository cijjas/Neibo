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

    private final ProfessionWorkerService ps;

    @Context
    private UriInfo uriInfo;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @Autowired
    public ProfessionController(final ProfessionWorkerService professionWorkerService) {
        this.ps = professionWorkerService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProfessions(
            @QueryParam("workerId") final Long workerId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/professions'");
        List<Profession> professions = ps.getWorkerProfessions(workerId);
        if (professions.isEmpty())
            return Response.noContent().build();

        List<ProfessionDto> professionDto = professions.stream()
                .map(p -> ProfessionDto.fromProfession(p, uriInfo)).collect(Collectors.toList());

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");

        return Response.ok(new GenericEntity<List<ProfessionDto>>(professionDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findProfession(@PathParam("id") final long id,
                                   @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                   @Context Request request) {
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
