package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AffiliationService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.webapp.dto.AffiliationDto;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.form.AffiliationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("/affiliations")
@Component
public class AffiliationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AffiliationController.class);

    @Autowired
    private AffiliationService nws;

    @Autowired WorkerService ws;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAffiliations(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("inNeighborhood") Long neighborhoodId,
            @QueryParam("forWorker") Long workerId
    ) {
        LOGGER.info("GET request arrived at '/affiliations'");

        Set<Affiliation> affiliations = nws.getAffiliations(workerId, neighborhoodId, page, size);

        if (affiliations.isEmpty())
            return Response.noContent().build();

        List<AffiliationDto> affiliationDto = affiliations.stream()
                .map(wa -> AffiliationDto.fromAffiliation(wa, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "affiliations/";
        int totalAmenityPages = nws.calculateAffiliationPages(workerId, neighborhoodId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalAmenityPages);
        return Response.ok(new GenericEntity<List<AffiliationDto>>(affiliationDto){})
                .links(links)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response addAffiliation(
            @Valid final AffiliationForm form
    ) {
        LOGGER.info("PATCH request arrived at '/affiliations'");
        Affiliation a = nws.createAffiliation(form.getWorkerURN(), form.getNeighborhoodURN(), form.getWorkerRole());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(a.getWorker().getWorkerId())).build();
        return Response.created(uri).build();
    }

    @PATCH
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response updateAffiliation(
            @Valid final AffiliationForm form
    ) {
        LOGGER.info("PATCH request arrived at '/affiliations'");

        Affiliation affiliation = nws.updateAffiliation(form.getWorkerURN(), form.getNeighborhoodURN(), form.getWorkerRole());
        return Response.ok(AffiliationDto.fromAffiliation(affiliation, uriInfo))
                .build();
    }

    @DELETE
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response removeWorkerFromNeighborhood(
            @Valid final AffiliationForm form
    ) {
        LOGGER.info("DELETE request arrived at '/affiliations'");

        if(nws.deleteAffiliation(form.getWorkerURN(), form.getNeighborhoodURN())) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
