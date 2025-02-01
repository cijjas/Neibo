package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.TransactionTypeDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.constants.Constant.MAX_AGE_SECONDS;

/*
 * # Summary
 *   - A Purchase has a Transaction Type which can be used to filter the Purchases, this is kind of conflicting with the Product Status enum
 *
 * # Use cases
 *   - A Neighbor/Admin filters the Requests through the Transaction Types
 */

@Path(Endpoint.API + "/" + Endpoint.TRANSACTION_TYPES)
@Component
@Produces(value = {MediaType.APPLICATION_JSON})
public class TransactionTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTypeController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    public Response listTransactionTypes() {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

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

        return Response.ok(new GenericEntity<List<TransactionTypeDto>>(transactionDto) {
                })
                .cacheControl(cacheControl)
                .tag(transactionTypesHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.TRANSACTION_TYPE_ID + "}")
    public Response findTransactionType(
            @PathParam(PathParameter.TRANSACTION_TYPE_ID) @GenericIdConstraint Long transactionTypeId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        TransactionType transactionType = TransactionType.fromId(transactionTypeId);
        String transactionTypeHashCode = String.valueOf(transactionType.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(transactionTypeHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(TransactionTypeDto.fromTransactionType(transactionType, uriInfo))
                .cacheControl(cacheControl)
                .tag(transactionTypeHashCode)
                .build();
    }
}
