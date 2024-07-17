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

/*
* # Summary
*   - An Affiliation is the Junction Table between Workers and Neighborhoods
*   - The relationship has the attribute that defines the state of that relationship (WORKER ROLE : VERIFIED_WORKER, UNVERIFIED_WORKER, REJECTED)
*
* # Use cases
*   - The Admin lists the Workers that have a relationship with his Neighborhood
*   - The Admin rejects or verifies a Worker
*   - The Worker creates an affiliation with UNVERIFIED status when it requests for a certain Neighborhood
*   - The Worker lists the neighborhoods he has an affiliation with
*
* # Embeddable? Don't think so
*   - No Embed :
*       The Admin panel should list the paginated affiliations for his Worker, and resolve the reference to each Worker
*   - Embed :
*       An Affiliation is both used from the Neighborhood and the Worker POV, does not appear to be embeddable as an attribute in neither of them
*       Also the affiliation itself has another attribute which makes the handling much harder
*       Worker {
*           name : Jorge,
*           professions : [prof1, prof2, prof3],
*           affiliations : [{neighborhood: nh1, status: verified}, {neighborhood: nh2, status: verified}, {neighborhood: nh3, status: verified}]
*       }
*       Use cases cant be satisfied!
*/

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
            @QueryParam("inNeighborhood") String neighborhoodURN,
            @QueryParam("forWorker")String workerURN
    ) {
        LOGGER.info("GET request arrived at '/affiliations'");

        // Content
        List<Affiliation> affiliations = nws.getAffiliations(workerURN, neighborhoodURN, page, size);
        String affiliationsHashCode = String.valueOf(affiliations.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(affiliationsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (affiliations.isEmpty())
            return Response.noContent()
                    .tag(affiliationsHashCode)
                    .build();

        List<AffiliationDto> affiliationDto = affiliations.stream()
                .map(wa -> AffiliationDto.fromAffiliation(wa, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "affiliations/",
                nws.calculateAffiliationPages(workerURN, neighborhoodURN, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<AffiliationDto>>(affiliationDto) {})
                .cacheControl(cacheControl)
                .tag(affiliationsHashCode)
                .links(links)
                .build();
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response addAffiliation(
            @Valid @NotNull final AffiliationForm form
    ) {
        LOGGER.info("POST request arrived at '/affiliations'");

        // Creation & HashCode Generation
        Affiliation affiliation = nws.createAffiliation(form.getWorkerURN(), form.getNeighborhoodURN(), form.getWorkerRoleURN());
        String affiliationHashCode = String.valueOf(affiliation.hashCode());

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(affiliation.getWorker().getWorkerId())).build();

        return Response.created(uri)
                .tag(affiliationHashCode)
                .build();
    }

    @PATCH
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canUpdateAffiliation(#form.neighborhoodURN)")
    public Response updateAffiliation(
            @Valid @NotNull final AffiliationForm form
    ) {
        LOGGER.info("PATCH request arrived at '/affiliations'");

        // Modification & HashCode Generation
        final Affiliation updatedAffiliation = nws.updateAffiliation(form.getWorkerURN(), form.getNeighborhoodURN(), form.getWorkerRoleURN());
        String updatedAffiliationHashCode = String.valueOf(updatedAffiliation.hashCode());

        return Response.ok(AffiliationDto.fromAffiliation(updatedAffiliation, uriInfo))
                .tag(updatedAffiliationHashCode)
                .build();
    }

    @DELETE
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response removeWorkerFromNeighborhood(
            @Valid @NotNull final AffiliationForm form
    ) {
        LOGGER.info("DELETE request arrived at '/affiliations'");

        // Deletion Attempt
        if (nws.deleteAffiliation(form.getWorkerURN(), form.getNeighborhoodURN())) {
            return Response.noContent()
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
