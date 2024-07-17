package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PurchaseService;
import ar.edu.itba.paw.models.Entities.Amenity;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

/*
 * # Summary
 *   - A Purchase is the represents the Junction Table between Products and Users
 *
 * # Use cases
 *   - A User can make a Purchase for a certain Product, the Seller has to specify that the Product was sold to that specific User
 *   - A User can list his Transactions (SOLD, BOUGHT)
 */

@Path("neighborhoods/{neighborhoodId}/users/{userId}/transactions")
@Component
public class PurchaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseController.class);

    @Autowired
    private PurchaseService ps;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private long neighborhoodId;

    @PathParam("userId")
    private long userId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize("@accessControlHelper.canAccessTransactions(#userId)")
    public Response listTransactions(
            @QueryParam("withType") String typeURN,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @PathParam("userId") final long userId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}/transactions'", neighborhoodId, userId);

        // Content
        List<Purchase> transactions = ps.getPurchases(userId, typeURN, page, size, neighborhoodId);
        String transactionsHashCode = String.valueOf(transactions.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(transactionsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (transactions.isEmpty())
            return Response.noContent()
                    .tag(transactionsHashCode)
                    .build();

        List<PurchaseDto> transactionDto = transactions.stream()
                .map(p -> PurchaseDto.fromPurchase(p, uriInfo))
                .collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/users/" + userId + "/transactions",
                ps.calculatePurchasePages(userId, typeURN, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<PurchaseDto>>(transactionDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(transactionsHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAccessTransactions(#userId)")
    public Response findTransaction(
            @PathParam("id") final long transactionId,
            @PathParam("userId") final long userId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}/transactions/{}'", neighborhoodId, userId, transactionId);

        // Content
        Purchase purchase = ps.findPurchase(transactionId, userId, neighborhoodId).orElseThrow(() -> new NotFoundException("Purchase not found"));
        String purchaseHashCode = String.valueOf(purchase.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(purchaseHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(PurchaseDto.fromPurchase(purchase, uriInfo))
                .cacheControl(cacheControl)
                .tag(purchaseHashCode)
                .build();
    }
}
