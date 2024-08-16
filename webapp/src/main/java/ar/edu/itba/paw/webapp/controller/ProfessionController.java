package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ProfessionService;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.form.NewProfessionForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
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
    @PreAuthorize("@accessControlHelper.hasAccessProfessionsQP(#worker)")
    public Response listProfessions(
            @QueryParam("forWorker") final String worker
    ) {
        LOGGER.info("GET request arrived at '/professions'");

        // Content
        List<Profession> professions = ps.getProfessions(worker);
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
                .map(p -> ProfessionDto.fromProfession(p, uriInfo)).collect(Collectors.toList());

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
        Profession profession = ps.findProfession(id).orElseThrow(() -> new NotFoundException("Profession Not Found"));
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

    @POST
    @Secured({"ROLE_SUPER_ADMINISTRATOR"})
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createProfession(
            @Valid @NotNull NewProfessionForm form
    ) {
        LOGGER.info("POST request arrived at '/professions'");

        // Content
        final Profession profession = ps.createProfession(form.getName());
        String professionHashCode = String.valueOf(profession.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(profession.getProfessionId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(professionHashCode)
                .build();
    }
}
