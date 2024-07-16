package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.webapp.dto.ProductStatusDto;
import ar.edu.itba.paw.webapp.dto.TimeDto;
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


    private final EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProductStatuses() {
        LOGGER.info("GET request arrived at '/product-statuses'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<ProductStatusDto> productStatusDto = Arrays.stream(ProductStatus.values())
                .map(tt -> ProductStatusDto.fromProductStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ProductStatusDto>>(productStatusDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findProductStatus(
            @PathParam("id") final int id,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/product-statuses/{}'", id);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(Long.toString(id));
        Response response = checkETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Content
        ProductStatusDto productStatusDto = ProductStatusDto.fromProductStatus(ProductStatus.fromId(id), uriInfo);

        return Response.ok(productStatusDto)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                .build();
    }
}