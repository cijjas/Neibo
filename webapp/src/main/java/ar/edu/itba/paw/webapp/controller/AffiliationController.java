package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AffiliationService;
import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.AffiliationDto;
import ar.edu.itba.paw.webapp.validation.constraints.urn.NeighborhoodURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.WorkerURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalFirstId;

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

@Path(Endpoint.AFFILIATIONS)
@Validated
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class AffiliationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AffiliationController.class);
    private final AffiliationService nws;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public AffiliationController(AffiliationService nws) {
        this.nws = nws;
    }

    @GET
    public Response listAffiliations(
            @QueryParam(QueryParameter.IN_NEIGHBORHOOD) @NeighborhoodURNConstraint String neighborhood,
            @QueryParam(QueryParameter.FOR_WORKER) @WorkerURNConstraint String worker,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '/affiliations'");

        // ID Extraction
        Long neighborhoodId = extractOptionalFirstId(neighborhood);
        Long workerId = extractOptionalFirstId(worker);

        // Content
        List<Affiliation> affiliations = nws.getAffiliations(neighborhoodId, workerId, page, size);
        String affiliationsHashCode;

        // This is required to keep a consistent hash code across creates and this endpoint used as a "find"
        if (affiliations.size() == 1) {
            Affiliation singleAffiliation = affiliations.get(0);
            affiliationsHashCode = String.valueOf(singleAffiliation.hashCode());
        } else {
            affiliationsHashCode = String.valueOf(affiliations.hashCode());
        }
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
                uriInfo.getBaseUriBuilder().path(Endpoint.AFFILIATIONS),
                nws.calculateAffiliationPages(neighborhoodId, workerId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<AffiliationDto>>(affiliationDto) {
                })
                .cacheControl(cacheControl)
                .tag(affiliationsHashCode)
                .links(links)
                .build();
    }

    @POST
    @Validated(CreateValidationSequence.class)
    public Response createAffiliation(
            @Valid @NotNull AffiliationDto createForm
    ) {
        LOGGER.info("POST request arrived at '/affiliations'");

        // Creation & HashCode Generation
        Affiliation affiliation = nws.createAffiliation(extractFirstId(createForm.getNeighborhood()), extractFirstId(createForm.getWorker()), extractFirstId(createForm.getWorkerRole()));
        String affiliationHashCode = String.valueOf(affiliation.hashCode());

        // Resource URN
        AffiliationDto affiliationDto = AffiliationDto.fromAffiliation(affiliation, uriInfo);

        return Response.created(affiliationDto.get_links().getSelf())
                .tag(affiliationHashCode)
                .build();
    }

    @PATCH
    @PreAuthorize("@pathAccessControlHelper.canUpdateAffiliation(#neighborhood)")
    @Validated(UpdateValidationSequence.class)
    public Response updateAffiliation(
            @QueryParam(QueryParameter.IN_NEIGHBORHOOD) @NotNull @NeighborhoodURNConstraint String neighborhood,
            @QueryParam(QueryParameter.FOR_WORKER) @NotNull @WorkerURNConstraint String worker,
            @Valid @NotNull AffiliationDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '/affiliations'");

        // Modification & HashCode Generation
        final Affiliation updatedAffiliation = nws.updateAffiliation(extractFirstId(neighborhood), extractFirstId(worker), extractFirstId(updateForm.getWorkerRole()));
        String updatedAffiliationHashCode = String.valueOf(updatedAffiliation.hashCode());

        return Response.ok(AffiliationDto.fromAffiliation(updatedAffiliation, uriInfo))
                .tag(updatedAffiliationHashCode)
                .build();
    }

    @DELETE
    @PreAuthorize("@pathAccessControlHelper.canDeleteAffiliation(#worker)")
    public Response deleteAffiliation(
            @QueryParam(QueryParameter.IN_NEIGHBORHOOD) @NotNull @NeighborhoodURNConstraint String neighborhood,
            @QueryParam(QueryParameter.FOR_WORKER) @NotNull @WorkerURNConstraint String worker
    ) {
        LOGGER.info("DELETE request arrived at '/affiliations'");

        // Deletion Attempt
        if (nws.deleteAffiliation(extractFirstId(neighborhood), extractFirstId(worker)))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
