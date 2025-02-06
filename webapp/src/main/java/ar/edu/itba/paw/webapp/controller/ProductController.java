package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.dto.queryForms.ProductParams;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.*;

/*
 * # Summary
 *   - Main Entity of the Marketplace functionality
 *   - Has many relationships, Inquiries, Requests, Purchases, Images
 *
 * # Use cases
 *   - A Admin can create, update and delete any Product in their Neighborhood
 *   - A Neighbor can create a Product
 *   - A Neighbor can update and delete a Product if it belongs to them
 *   - A Neighbor/Admin can list the Products in its Neighborhood
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.PRODUCTS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON})
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    private final ProductService ps;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public ProductController(ProductService ps) {
        this.ps = ps;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canListProducts(#productParams.user, #productParams.department, #productParams.productStatus)")
    public Response listProducts(
            @Valid @BeanParam ProductParams productParams
            ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long userId = extractOptionalFirstId(productParams.getUser());
        Long departmentId = extractOptionalFirstId(productParams.getDepartment());
        Long productStatusId = extractOptionalFirstId(productParams.getProductStatus());

        // Content
        final List<Product> products = ps.getProducts(productParams.getNeighborhoodId(), userId, departmentId, productStatusId, productParams.getPage(), productParams.getSize());
        String productsHashCode = String.valueOf(products.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(productsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (products.isEmpty())
            return Response.noContent()
                    .tag(productsHashCode)
                    .build();

        final List<ProductDto> productsDto = products.stream()
                .map(p -> ProductDto.fromProduct(p, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(productParams.getNeighborhoodId())).path(Endpoint.PRODUCTS),
                ps.calculateProductPages(productParams.getNeighborhoodId(), userId, departmentId, productStatusId, productParams.getSize()),
                productParams.getPage(),
                productParams.getSize()
        );

        return Response.ok(new GenericEntity<List<ProductDto>>(productsDto) {
                })
                .cacheControl(cacheControl)
                .tag(productsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("{" + PathParameter.PRODUCT_ID + "}")
    public Response findProduct(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) long productId
    ) {
        LOGGER.info("GET request arrived '{}'", uriInfo.getRequestUri());

        // Content
        Product product = ps.findProduct(neighborhoodId, productId).orElseThrow(NotFoundException::new);
        String productHashCode = String.valueOf(product.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(productHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ProductDto.fromProduct(product, uriInfo))
                .cacheControl(cacheControl)
                .tag(productHashCode)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateProduct(#createForm.user, #createForm.department)")
    public Response createProduct(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @NotNull ProductDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & ETag Generation
        final Product product = ps.createProduct(extractFirstId(createForm.getUser()), createForm.getName(), createForm.getDescription(), createForm.getPrice(), createForm.getRemainingUnits(), createForm.getUsed(), extractFirstId(createForm.getDepartment()), extractFirstIds(createForm.getImages()));
        String productHashCode = String.valueOf(product.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(product.getProductId())).build();

        return Response.created(uri)
                .tag(productHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.PRODUCT_ID + "}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canUpdateProduct(#updateForm.user, #updateForm.department, #productId)")
    @Validated(UpdateSequence.class)
    public Response updateProduct(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) long productId,
            @Valid @NotNull ProductDto updateForm
    ) {
        LOGGER.info("UPDATE request arrived at '{}'", uriInfo.getRequestUri());

        // Modification & HashCode Generation
        final Product updatedProduct = ps.updateProduct(
                neighborhoodId,
                productId,
                updateForm.getName(),
                updateForm.getDescription(),
                updateForm.getPrice(),
                updateForm.getRemainingUnits(), updateForm.getUsed(),
                extractOptionalFirstId(updateForm.getDepartment()),
                extractFirstIds(updateForm.getImages())
        );
        String productHashCode = String.valueOf(updatedProduct.hashCode());

        return Response.ok(ProductDto.fromProduct(updatedProduct, uriInfo))
                .tag(productHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.PRODUCT_ID + "}")
    @PreAuthorize("@accessControlHelper.canDeleteProduct(#productId)")
    public Response deleteProduct(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) long productId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Attempt to delete the amenity
        if (ps.deleteProduct(neighborhoodId, productId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
