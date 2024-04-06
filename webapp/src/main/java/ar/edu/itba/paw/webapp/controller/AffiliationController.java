package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AffiliationService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.webapp.dto.AffiliationDto;
import ar.edu.itba.paw.webapp.form.AffiliationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.CUSTOM_ROW_LEVEL_ETAG_NAME;

@Path("/affiliations")
@Component
public class AffiliationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AffiliationController.class);

    @Autowired
    private AffiliationService nws;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listAffiliations(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("inNeighborhood") Long neighborhoodId,
            @QueryParam("forWorker") Long workerId
    ) {
        LOGGER.info("GET request arrived at '/affiliations'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<Affiliation> affiliations = nws.getAffiliations(workerId, neighborhoodId, page, size);
        if (affiliations.isEmpty())
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        List<AffiliationDto> affiliationDto = affiliations.stream()
                .map(wa -> AffiliationDto.fromAffiliation(wa, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "affiliations/",
                nws.calculateAffiliationPages(workerId, neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<AffiliationDto>>(affiliationDto) {})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .links(links)
                .build();
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response addAffiliation(
            @Valid @NotNull final AffiliationForm form
    ) {
        LOGGER.info("PATCH request arrived at '/affiliations'");

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .tag(entityLevelETag)
                    .build();

        // Creation & ETag Generation
        Affiliation affiliation = nws.createAffiliation(form.getWorkerURN(), form.getNeighborhoodURN(), form.getWorkerRole());
        entityLevelETag = ETagUtility.generateETag();

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(affiliation.getWorker().getWorkerId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .build();
    }

    @PATCH
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canUpdateAffiliation(#form.neighborhoodURN)")
    public Response updateAffiliation(
            @Valid @NotNull final AffiliationForm form,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/affiliations'");

        // Cache Control
        if (ifMatch != null) {
            Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .tag(entityLevelETag)
                        .build();
        }

        // Modification & ETag Generation
        AffiliationDto affiliationDto = AffiliationDto.fromAffiliation(nws.updateAffiliation(form.getWorkerURN(), form.getNeighborhoodURN(), form.getWorkerRole()), uriInfo);
        entityLevelETag = ETagUtility.generateETag();

        return Response.ok(affiliationDto)
                .tag(entityLevelETag)
                .build();
    }

    @DELETE
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response removeWorkerFromNeighborhood(
            @Valid @NotNull final AffiliationForm form,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/affiliations'");

        // Cache Control
        if (ifMatch != null) {
            Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .tag(entityLevelETag)
                        .build();
        }

        // Deletion & Etag Generation Attempt
        if (nws.deleteAffiliation(form.getWorkerURN(), form.getNeighborhoodURN())) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .tag(entityLevelETag)
                .build();
    }
}
