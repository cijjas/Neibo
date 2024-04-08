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
            @QueryParam("withType") String type,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @PathParam("userId") final long userId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}/transactions'", neighborhoodId, userId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<Purchase> transactions = ps.getPurchases(userId, type, page, size, neighborhoodId);
        if (transactions.isEmpty())
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        List<PurchaseDto> transactionDto = transactions.stream()
                .map(p -> PurchaseDto.fromPurchase(p, uriInfo))
                .collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/users/" + userId + "/transactions",
                ps.calculatePurchasePages(userId, type, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<PurchaseDto>>(transactionDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAccessTransactions(#userId)")
    public Response findTransaction(
            @PathParam("id") final long transactionId,
            @PathParam("userId") final long userId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}/transactions/{}'", neighborhoodId, userId, transactionId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(Long.toString(transactionId));
        Response response = checkETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Content
        PurchaseDto purchaseDto = PurchaseDto.fromPurchase(ps.findPurchase(transactionId, userId, neighborhoodId).orElseThrow(NotFoundException::new), uriInfo);

        return Response.ok(purchaseDto)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                .build();
    }
}
