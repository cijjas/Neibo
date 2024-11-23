package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.webapp.dto.ProductStatusDto;
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

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.MAX_AGE_SECONDS;


/*
 * # Summary
 *   - Product Criteria, a Product can be BOUGHT, SOLD, SELLING, these are used as a filter and require an extra User Query Param
 *
 * # Use cases
 *   - A User can filter the Products over these criteria, useful for creating the "My Products Sold" "Products I Bought" views
 */

@Path("product-statuses")
@Component
public class ProductStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductStatusController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProductStatuses() {
        LOGGER.info("GET request arrived at '/product-statuses'");

        // Content
        ProductStatus[] productStatuses = ProductStatus.values();
        String productStatusesHashCode = String.valueOf(Arrays.hashCode(productStatuses));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(productStatusesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        List<ProductStatusDto> productStatusDto = Arrays.stream(productStatuses)
                .map(tt -> ProductStatusDto.fromProductStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ProductStatusDto>>(productStatusDto) {
                })
                .cacheControl(cacheControl)
                .tag(productStatusesHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response findProductStatus(
            @PathParam("id") @GenericIdConstraint final Long id
    ) {
        LOGGER.info("GET request arrived at '/product-statuses/{}'", id);

        // Content
        ProductStatus productStatus = ProductStatus.fromId(id);
        String productStatusHashCode = String.valueOf(productStatus.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(productStatusHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ProductStatusDto.fromProductStatus(productStatus, uriInfo))
                .cacheControl(cacheControl)
                .tag(productStatusHashCode)
                .build();
    }
}