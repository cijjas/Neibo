package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AffiliationService;
import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.dto.AffiliationDto;
import ar.edu.itba.paw.webapp.dto.queryForms.AffiliationParams;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.DeleteSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.GetSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateSequence;
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
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractNullableFirstId;

/*
 * # Summary
 *   - An Affiliation is the Junction Table between Workers and Neighborhoods
 *   - The relationship has the attribute that defines the state of that relationship (WORKER ROLE : VERIFIED_WORKER, UNVERIFIED_WORKER, REJECTED)
 *
 * # Use cases
 *   - The Admin can list the Workers that have a relationship with his Neighborhood
 *   - The Admin can reject or verify a Worker
 *   - The Worker can create an Affiliation with UNVERIFIED status when it requests for a certain Neighborhood
 *   - The Worker can list the Neighborhoods he has an Affiliation with
 */

@Path(Endpoint.API + "/" + Endpoint.AFFILIATIONS)
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
    @Validated(GetSequence.class)
    @PreAuthorize("@accessControlHelper.canListAffiliations(#affiliationParams.neighborhood, #affiliationParams.worker)")
    public Response listAffiliations(
            @Valid @BeanParam AffiliationParams affiliationParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long neighborhoodId = extractNullableFirstId(affiliationParams.getNeighborhood());
        Long workerId = extractNullableFirstId(affiliationParams.getWorker());

        // Content
        List<Affiliation> affiliations = nws.getAffiliations(neighborhoodId, workerId, affiliationParams.getPage(), affiliationParams.getSize());
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
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.AFFILIATIONS),
                nws.calculateAffiliationPages(neighborhoodId, workerId, affiliationParams.getSize()),
                affiliationParams.getPage(),
                affiliationParams.getSize()
        );

        return Response.ok(new GenericEntity<List<AffiliationDto>>(affiliationDto) {
                })
                .cacheControl(cacheControl)
                .tag(affiliationsHashCode)
                .links(links)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateAffiliation(#createForm.neighborhood, #createForm.worker, #createForm.workerRole)")
    public Response createAffiliation(
            @Valid @NotNull AffiliationDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        Affiliation affiliation = nws.createAffiliation(extractFirstId(createForm.getNeighborhood()), extractFirstId(createForm.getWorker()), extractFirstId(createForm.getWorkerRole()));
        String affiliationHashCode = String.valueOf(affiliation.hashCode());

        // Resource URI
        AffiliationDto affiliationDto = AffiliationDto.fromAffiliation(affiliation, uriInfo);

        return Response.created(affiliationDto.get_links().getSelf())
                .tag(affiliationHashCode)
                .build();
    }

    @PATCH
    @PreAuthorize("@accessControlHelper.canUpdateAffiliation(#affiliationParams.neighborhood, #affiliationParams.worker, #updateForm.workerRole)")
    @Validated(UpdateSequence.class)
    public Response updateAffiliation(
            @Valid @BeanParam AffiliationParams affiliationParams,
            @Valid @NotNull AffiliationDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '{}'", uriInfo.getRequestUri());

        // Modification & HashCode Generation
        final Affiliation updatedAffiliation = nws.updateAffiliation(extractFirstId(affiliationParams.getNeighborhood()), extractFirstId(affiliationParams.getWorker()), extractFirstId(updateForm.getWorkerRole()));
        String updatedAffiliationHashCode = String.valueOf(updatedAffiliation.hashCode());

        return Response.ok(AffiliationDto.fromAffiliation(updatedAffiliation, uriInfo))
                .tag(updatedAffiliationHashCode)
                .build();
    }

    @DELETE
    @PreAuthorize("@accessControlHelper.canDeleteAffiliation(#affiliationParams.neighborhood, #affiliationParams.worker)")
    @Validated(DeleteSequence.class)
    public Response deleteAffiliation(
            @Valid @BeanParam AffiliationParams affiliationParams
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (nws.deleteAffiliation(extractFirstId(affiliationParams.getNeighborhood()), extractFirstId(affiliationParams.getWorker())))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
