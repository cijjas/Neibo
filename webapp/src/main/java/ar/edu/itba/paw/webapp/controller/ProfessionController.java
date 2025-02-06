package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ProfessionService;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.controller.constants.UserRole;
import ar.edu.itba.paw.webapp.dto.ProfessionDto;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalFirstId;


/*
 * # Summary
 *   - A Worker can have many Professions
 *
 * # Use cases
 *   - The Workers can choose which Professions they have
 *   - A Neighbor/Admin can filter the Workers through certain Profession
 */

@Path(Endpoint.API + "/" + Endpoint.PROFESSIONS)
@Component
@Validated
@Produces(MediaType.APPLICATION_JSON)
public class ProfessionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionController.class);
    private final ProfessionService ps;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public ProfessionController(ProfessionService ps) {
        this.ps = ps;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canUseWorkerQPInProfessions(#worker)")
    public Response listProfessions(
            @QueryParam(QueryParameter.FOR_WORKER) String worker
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        List<Profession> professions = ps.getProfessions(extractOptionalFirstId(worker));
        String professionsHashCode = String.valueOf(professions.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(professionsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (professions.isEmpty())
            return Response.noContent()
                    .tag(professionsHashCode)
                    .build();

        List<ProfessionDto> professionDto = professions.stream()
                .map(p -> ProfessionDto.fromProfession(p, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ProfessionDto>>(professionDto) {
                })
                .cacheControl(cacheControl)
                .tag(professionsHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.PROFESSION_ID + "}")
    public Response findProfession(
            @PathParam(PathParameter.PROFESSION_ID) long professionId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Profession profession = ps.findProfession(professionId).orElseThrow(NotFoundException::new);
        String professionHashCode = String.valueOf(profession.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(professionHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ProfessionDto.fromProfession(profession, uriInfo))
                .cacheControl(cacheControl)
                .tag(professionHashCode)
                .build();
    }

    @POST
    @Secured(UserRole.SUPER_ADMINISTRATOR)
    @Validated(CreateSequence.class)
    public Response createProfession(
            @Valid @NotNull ProfessionDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        final Profession profession = ps.createProfession(createForm.getName());
        String professionHashCode = String.valueOf(profession.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(profession.getProfessionId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(professionHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.PROFESSION_ID + "}")
    @Secured(UserRole.SUPER_ADMINISTRATOR)
    public Response deleteProfession(
            @PathParam(PathParameter.PROFESSION_ID) long professionId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (ps.deleteProfession(professionId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
