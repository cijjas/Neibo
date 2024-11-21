package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ProfessionService;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.webapp.dto.ProfessionDto;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.WorkerURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


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
@Validated
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
    @PreAuthorize("@accessControlHelper.canUseWorkerQPInProfessions(#worker)")
    public Response listProfessions(
            @QueryParam("forWorker") @WorkerURNFormConstraint @WorkerURNReferenceConstraint final String worker
    ) {
        LOGGER.info("GET request arrived at '/professions'");

        // Content
        List<Profession> professions = ps.getProfessions(worker);
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

        return Response.ok(new GenericEntity<List<ProfessionDto>>(professionDto) {})
                .cacheControl(cacheControl)
                .tag(professionsHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response findProfession(
            @PathParam("id") final long id
    ) {
        LOGGER.info("GET request arrived at '/professions/{}'", id);

        // Content
        Profession profession = ps.findProfession(id).orElseThrow(() -> new NotFoundException("Profession Not Found"));
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
    @Secured({"ROLE_SUPER_ADMINISTRATOR"})
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Validated(CreateValidationSequence.class)
    public Response createProfession(
            @Valid ProfessionDto form
    ) {
        LOGGER.info("POST request arrived at '/professions'");

        // Content
        final Profession profession = ps.createProfession(form.getProfession());
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

    @DELETE
    @Path("/{id}")
    @Secured({"ROLE_SUPER_ADMINISTRATOR"})
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response deleteProfessionById(
            @PathParam("id") final long id
    ) {
        LOGGER.info("DELETE request arrived at '/professions/{}'", id);

        // Deletion Attempt
        if (ps.deleteProfession(id))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
