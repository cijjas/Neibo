package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.DepartmentURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ProductStatusURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
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
 *   - A User/Admin can create/update/delete a Product in their Neighborhood
 *   - A User/Admin can list the Products in its Neighborhood
 */

@Path(Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.PRODUCTS)
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
    public Response listProducts(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @QueryParam(QueryParameter.FOR_USER) @UserURNConstraint String user,
            @QueryParam(QueryParameter.IN_DEPARTMENT) @DepartmentURNConstraint String department,
            @QueryParam(QueryParameter.WITH_STATUS) @ProductStatusURNConstraint String productStatus,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/products'", neighborhoodId);

        // ID Extraction
        Long userId = extractOptionalSecondId(user);
        Long departmentId = extractOptionalFirstId(department);
        Long productStatusId = extractOptionalFirstId(productStatus);

        // Content
        final List<Product> products = ps.getProducts(neighborhoodId, userId, departmentId, productStatusId, page, size);
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
                uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.PRODUCTS),
                ps.calculateProductPages(neighborhoodId, userId, departmentId, productStatusId, size),
                page,
                size
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
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) @GenericIdConstraint long productId
    ) {
        LOGGER.info("GET request arrived '/neighborhoods/{}/products/{}'", neighborhoodId, productId);

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
    @Validated(CreateValidationSequence.class)
    public Response createProduct(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @Valid @NotNull ProductDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/products'", neighborhoodId);

        // Creation & ETag Generation
        final Product product = ps.createProduct(extractSecondId(createForm.getUser()), createForm.getName(), createForm.getDescription(), createForm.getPrice(), createForm.getRemainingUnits(), createForm.getUsed(), extractFirstId(createForm.getDepartment()), extractFirstIds(createForm.getImages()));
        String productHashCode = String.valueOf(product.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(product.getProductId())).build();

        return Response.created(uri)
                .tag(productHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.PRODUCT_ID + "}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@pathAccessControlHelper.canUpdateProduct(#productId)")
    @Validated(UpdateValidationSequence.class)
    public Response updateProduct(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) @GenericIdConstraint long productId,
            @Valid @NotNull ProductDto updateForm
    ) {
        LOGGER.info("UPDATE request arrived at '/neighborhoods/{}/products/{}'", neighborhoodId, productId);

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
    @PreAuthorize("@pathAccessControlHelper.canDeleteProduct(#productId)")
    public Response deleteProduct(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) @GenericIdConstraint long productId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/products/{}'", neighborhoodId, productId);

        // Attempt to delete the amenity
        if (ps.deleteProduct(neighborhoodId, productId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
