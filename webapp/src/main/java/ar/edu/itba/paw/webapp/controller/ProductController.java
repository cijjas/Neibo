package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.form.ListingForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/products")
@Component
public class ProductController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService ps;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;


    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @Autowired
    public ProductController(final UserService us) {
        super(us);
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listProducts(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("inDepartment") final String department,
            @QueryParam("listedBy") final Long userId,
            @QueryParam("withStatus") final String productStatus
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/products'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Product> products = ps.getProducts(neighborhoodId, department, userId, productStatus, page, size);
        if (products.isEmpty())
            return Response.noContent().build();
        final List<ProductDto> productsDto = products.stream()
                .map(p -> ProductDto.fromProduct(p, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/products",
                ps.calculateProductPages(neighborhoodId, size, department, userId, productStatus),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ProductDto>>(productsDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findProduct(
            @PathParam("id") final long productId
    ) {
        LOGGER.info("GET request arrived '/neighborhoods/{}/products/{}'", neighborhoodId, productId);

        // Content
        Product product = ps.findProduct(productId, neighborhoodId).orElseThrow(() -> new NotFoundException("Product Not Found"));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(product.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ProductDto.fromProduct(product, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createProduct(
            @Valid final ListingForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/products'", neighborhoodId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final Product product = ps.createProduct(getLoggedUser().getUserId(), form.getTitle(), form.getDescription(), form.getPrice(), form.getUsed(), form.getDepartmentURN(), form.getImageFiles(), form.getQuantity());
        entityLevelETag = ETagUtility.generateETag();

        // Resource  URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(product.getProductId())).build();

        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response updateProductPartially(
            @PathParam("id") final long id,
            @Valid final ListingForm partialUpdate,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("UPDATE request arrived at '/neighborhoods/{}/products/{}'", neighborhoodId, id);

        // Cache Control
        if (ifMatch != null) {
            String version = ps.findProduct(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(version));

            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, version)
                        .build();
        }

        // Modification & ETag Generation
        final ProductDto productDto = ProductDto.fromProduct(ps.updateProductPartially(id, partialUpdate.getTitle(), partialUpdate.getDescription(), partialUpdate.getPrice(), partialUpdate.getUsed(), partialUpdate.getDepartmentURN(), partialUpdate.getImageFiles(), partialUpdate.getQuantity()), uriInfo);
        entityLevelETag = ETagUtility.generateETag();

        return Response.ok(productDto)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canDeleteProduct(#productId)")
    public Response deleteById(
            @PathParam("id") final long productId,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/products/{}'", neighborhoodId, productId);

        // Cache Control
        if (ifMatch != null) {
            String version = ps.findProduct(productId, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(version));
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, version)
                        .build();
        }

        // Deletion & ETag Generation Attempt
        if(ps.deleteProduct(productId)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
