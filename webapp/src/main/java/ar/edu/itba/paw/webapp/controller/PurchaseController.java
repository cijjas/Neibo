package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PurchaseService;
import ar.edu.itba.paw.models.Entities.Purchase;
import ar.edu.itba.paw.webapp.dto.PurchaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/users/{userId}/purchases")
@Component
public class PurchaseController {

    @Autowired
    private PurchaseService ps;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("userId")
    private Long userId;

    // In PurchaseController

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPurchasesByCriteria(
            @QueryParam("sellerId") long sellerId,
            @QueryParam("buyerId") long buyerId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        Set<Purchase> purchases = ps.getPurchasesByCriteria(sellerId, buyerId, page, size);

        // Convert purchases to DTOs if needed
        // Assuming there's a method to convert Purchase to PurchaseDto
        Set<PurchaseDto> purchaseDto = purchases.stream()
                .map(p -> PurchaseDto.fromPurchase(p, uriInfo))
                .collect(Collectors.toSet());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods" + neighborhoodId + "/requests";
        Link[] links = createPaginationLinks(baseUri, page, size, ps.getTotalPurchasesPages(sellerId, buyerId, size));

        return Response.ok(new GenericEntity<Set<PurchaseDto>>(purchaseDto){})
                .links(links)
                .build();
    }
}
