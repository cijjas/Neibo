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

@Path("transaction-types")
@Component
public class TransactionTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTypeController.class);

    @Context
    private UriInfo uriInfo;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTransactionTypes(
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/transaction-type'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<TransactionTypeDto> transactionDto = Arrays.stream(TransactionType.values())
                .map(tt -> TransactionTypeDto.fromTransactionType(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<TransactionTypeDto>>(transactionDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findTransactionType(
            @PathParam("id") final int id,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/transaction-type/{}'", id);
        TransactionTypeDto transactionTypeDto = TransactionTypeDto.fromTransactionType(TransactionType.fromId(id), uriInfo);

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");
        return Response.ok(transactionTypeDto)
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }
}
