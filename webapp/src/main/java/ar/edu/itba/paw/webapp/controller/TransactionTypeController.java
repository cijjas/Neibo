package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.webapp.dto.TransactionTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

/*
 * # Summary
 *   - A Purchase has a Transaction Type which can be used to filter the Purchases, this is kind of conflicting with the Product Status enum
 *
 * # Use cases
 *   - A User/Admin filters the Purchases/Transactions through the Transaction Types
 */

@Path("transaction-types")
@Component
public class TransactionTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTypeController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTransactionTypes() {
        LOGGER.info("GET request arrived at '/transaction-type'");

        // Content
        TransactionType[] transactionTypes = TransactionType.values();
        String transactionTypesHashCode = String.valueOf(Arrays.hashCode(transactionTypes));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(transactionTypesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<TransactionTypeDto> transactionDto = Arrays.stream(transactionTypes)
                .map(tt -> TransactionTypeDto.fromTransactionType(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<TransactionTypeDto>>(transactionDto){})
                .cacheControl(cacheControl)
                .tag(transactionTypesHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findTransactionType(
            @PathParam("id") final int id
    ) {
        LOGGER.info("GET request arrived at '/transaction-type/{}'", id);

        // Content
        TransactionType transactionType = TransactionType.fromId(id);
        String transactionTypeHashCode = String.valueOf(transactionType.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(transactionTypeHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(TransactionTypeDto.fromTransactionType(transactionType, uriInfo))
                .cacheControl(cacheControl)
                .tag(transactionTypeHashCode)
                .build();
    }
}
