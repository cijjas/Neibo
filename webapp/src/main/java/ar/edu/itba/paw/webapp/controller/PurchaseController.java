package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PurchaseService;
import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.models.Entities.Purchase;
import ar.edu.itba.paw.webapp.dto.CommentDto;
import ar.edu.itba.paw.webapp.dto.PurchaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/users/{userId}/transactions")
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactions(
            @QueryParam("type") String type,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        Set<Purchase> transactions;

        transactions = ps.getPurchasesByType(userId, type, page, size);

        // Convert transactions to DTOs if needed
        Set<PurchaseDto> transactionDto = transactions.stream()
                .map(p -> PurchaseDto.fromPurchase(p, uriInfo))
                .collect(Collectors.toSet());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/users/" + userId + "/transactions";
        Link[] links = createPaginationLinks(baseUri, page, size, ps.getTotalPurchasesPages(userId, userId, size));

        return Response.ok(new GenericEntity<Set<PurchaseDto>>(transactionDto){})
                .links(links)
                .build();
    }
}
