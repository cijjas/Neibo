package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.webapp.dto.DepartmentDto;
import ar.edu.itba.paw.webapp.dto.TimeDto;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTransactionTypes() {
        LOGGER.info("GET request arrived at '/transaction-type'");
        List<TransactionTypeDto> transactionDto = Arrays.stream(TransactionType.values())
                .map(tt -> TransactionTypeDto.fromTransactionType(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<TransactionTypeDto>>(transactionDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findTransactionType(@PathParam("id") final int id) {
        LOGGER.info("GET request arrived at '/transaction-type/{}'", id);
        return Response.ok(TransactionTypeDto.fromTransactionType(TransactionType.fromId(id), uriInfo)).build();
    }
}
