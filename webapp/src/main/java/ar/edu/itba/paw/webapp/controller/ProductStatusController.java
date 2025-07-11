package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.ProductStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.constants.Constant.IMMUTABLE;
import static ar.edu.itba.paw.webapp.controller.constants.Constant.MAX_AGE_SECONDS;

/*
 * # Summary
 *   - Product Criteria, a Product can be BOUGHT, SOLD, SELLING, these are used as a filter and require an extra User Query Param
 *
 * # Use cases
 *   - A Neighbor can filter the Products over these criteria
 */

@Path(Endpoint.API + "/" + Endpoint.PRODUCT_STATUSES)
@Component
@Produces(value = {MediaType.APPLICATION_JSON})
public class ProductStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductStatusController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    public Response listProductStatuses() {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        ProductStatus[] productStatuses = ProductStatus.values();
        String productStatusesHashCode = String.valueOf(Arrays.hashCode(productStatuses));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
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
    @Path("{" + PathParameter.PRODUCT_STATUS_ID + "}")
    public Response findProductStatus(
            @PathParam(PathParameter.PRODUCT_STATUS_ID) long productStatusId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        ProductStatus productStatus = ProductStatus.fromId(productStatusId).orElseThrow(NotFoundException::new);
        String productStatusHashCode = String.valueOf(productStatus.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(productStatusHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ProductStatusDto.fromProductStatus(productStatus, uriInfo))
                .cacheControl(cacheControl)
                .tag(productStatusHashCode)
                .build();
    }
}