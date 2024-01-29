package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PurchaseService;
import ar.edu.itba.paw.models.Entities.Purchase;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.PurchaseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/users/{userId}/transactions")
@Component
public class PurchaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseController.class);

    @Autowired
    private PurchaseService ps;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private long neighborhoodId;

    @PathParam("userId")
    private long userId;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize("@accessControlHelper.canAccessTransactions(#userId)")
    public Response listTransactions(
            @QueryParam("withType") String type,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @PathParam("userId") final long userId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}/transactions'", neighborhoodId, userId);

        Set<Purchase> transactions = ps.getPurchases(userId, type, page, size, neighborhoodId);
        if (transactions.isEmpty())
            return Response.noContent().build();

        Set<PurchaseDto> transactionDto = transactions.stream()
                .map(p -> PurchaseDto.fromPurchase(p, uriInfo))
                .collect(Collectors.toSet());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/users/" + userId + "/transactions";
        Link[] links = createPaginationLinks(baseUri, page, size, ps.calculatePurchasePages(userId, type, size));

        return Response.ok(new GenericEntity<Set<PurchaseDto>>(transactionDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAccessTransactions(#userId)")
    public Response findTransaction(@PathParam("id") final long transactionId,
                                    @PathParam("userId") final long userId) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}/transactions/{}'", neighborhoodId, userId, transactionId);
        return Response.ok(PurchaseDto.fromPurchase(ps.findPurchase(transactionId, userId, neighborhoodId)
                .orElseThrow(() -> new NotFoundException("Purchase Not Found")), uriInfo)).build();
    }
}
