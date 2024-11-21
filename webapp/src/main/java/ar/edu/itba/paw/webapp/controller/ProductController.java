package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.validation.constraints.form.DepartmentURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProductStatusURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.DepartmentURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ProductStatusURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.UserURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ValidationUtils.*;

/*
 * # Summary
 *   - Main Entity of the Marketplace functionality
 *   - Has many relationships, Inquiries, Requests, Purchases, Images
 *
 * # Use cases
 *   - A User/Admin can create/update/delete a Product in their Neighborhood
 *   - A User/Admin can list the Products in its Neighborhood
 */

@Path("neighborhoods/{neighborhoodId}/products")
@Component
@Validated
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService ps;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;


    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listProducts(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("inDepartment") @DepartmentURNFormConstraint @DepartmentURNReferenceConstraint final String department,
            @QueryParam("forUser") @UserURNFormConstraint @UserURNReferenceConstraint final String user,
            @QueryParam("withStatus") @ProductStatusURNFormConstraint @ProductStatusURNReferenceConstraint final String productStatus
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/products'", neighborhoodId);

        // Content
        final List<Product> products = ps.getProducts(neighborhoodId, department, user, productStatus, page, size);
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
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/products",
                ps.calculateProductPages(neighborhoodId, size, department, user, productStatus),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ProductDto>>(productsDto) {})
                .cacheControl(cacheControl)
                .tag(productsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findProduct(
            @PathParam("id") final long productId
    ) {
        LOGGER.info("GET request arrived '/neighborhoods/{}/products/{}'", neighborhoodId, productId);

        // Content
        Product product = ps.findProduct(productId, neighborhoodId).orElseThrow(() -> new NotFoundException("Product not found"));
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
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Validated(CreateValidationSequence.class)
    public Response createProduct(
            @Valid final ProductDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/products'", neighborhoodId);

        // Creation & ETag Generation
        final Product product = ps.createProduct(extractSecondId(form.getUser()), form.getName(), form.getDescription(), form.getPrice(), form.getUsed(), extractFirstId(form.getDepartment()), extractFirstIds(form.getImages()), form.getRemainingUnits());
        String productHashCode = String.valueOf(product.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(product.getProductId())).build();

        return Response.created(uri)
                .tag(productHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canUpdateProduct(#id)")
    @Validated(UpdateValidationSequence.class)
    public Response updateProductPartially(
            @PathParam("id") final long id,
            @Valid final ProductDto partialUpdate
    ) {
        LOGGER.info("UPDATE request arrived at '/neighborhoods/{}/products/{}'", neighborhoodId, id);

        // Modification & HashCode Generation
        final Product updatedProduct = ps.updateProductPartially(
                id,
                partialUpdate.getName(),
                partialUpdate.getDescription(),
                partialUpdate.getPrice(),
                partialUpdate.getUsed(),
                extractOptionalFirstId(partialUpdate.getDepartment()),
                extractFirstIds(partialUpdate.getImages()),
                partialUpdate.getRemainingUnits()
        );
        String productHashCode = String.valueOf(updatedProduct.hashCode());

        return Response.ok(ProductDto.fromProduct(updatedProduct, uriInfo))
                .tag(productHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canDeleteProduct(#productId)")
    public Response deleteById(
            @PathParam("id") final long productId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/products/{}'", neighborhoodId, productId);

        // Attempt to delete the amenity
        if (ps.deleteProduct(productId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
