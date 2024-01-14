package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.form.ListingForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ProductService ps;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @Autowired
    public ProductController(final UserService us, final ProductService ps) {
        super(us);
        this.ps = ps;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listProducts(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("department") @DefaultValue("NONE") final String department,
            @QueryParam("userId") @DefaultValue("0") final long userId,
            @QueryParam("productStatus") final String productStatus // BOUGHT/SOLD/SELLING
            ) {
        LOGGER.info("GET request arrived at neighborhoods/{}/products", neighborhoodId);
        final List<Product> products = ps.getProductsByCriteria(neighborhoodId, department, userId, productStatus, page, size);
        final List<ProductDto> productsDto = products.stream()
                .map(p -> ProductDto.fromProduct(p, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/products";
        int totalProductPages = ps.getProductsTotalPages(neighborhoodId, size, department, userId, productStatus);
        Link[] links = createPaginationLinks(baseUri, page, size, totalProductPages);

        return Response.ok(new GenericEntity<List<ProductDto>>(productsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findProduct(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived neighborhoods/{}/products/{}", neighborhoodId, id);
        return Response.ok(ProductDto.fromProduct(ps.findProductById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createProduct(@Valid final ListingForm form) {
        LOGGER.info("POST request arrived at neighborhoods/{}/products", neighborhoodId);
        final Product product = ps.createProduct(getLoggedUser().getUserId(), form.getTitle(), form.getDescription(), form.getPrice(), form.getUsed(), form.getDepartmentId(), form.getImageFiles(), form.getQuantity());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(product.getProductId())).build();
        return Response.created(uri).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response updateProductPartially(
            @PathParam("id") final long id,
            @Valid final ListingForm partialUpdate) {
        LOGGER.info("UPDATE request arrived at neighborhoods/{}/products/{}", neighborhoodId, id);
        final Product product = ps.updateProductPartially(id, partialUpdate.getTitle(), partialUpdate.getDescription(), partialUpdate.getPrice(), partialUpdate.getUsed(), partialUpdate.getDepartmentId(), partialUpdate.getImageFiles(), partialUpdate.getQuantity());
        return Response.ok(ProductDto.fromProduct(product, uriInfo)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        LOGGER.info("DELETE request arrived at neighborhoods/{}/products/{}", neighborhoodId, id);
        ps.deleteProduct(id);
        return Response.noContent().build();
    }
}
