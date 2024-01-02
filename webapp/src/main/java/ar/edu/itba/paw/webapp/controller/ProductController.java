package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/products")
@Component
public class ProductController {

    @Autowired
    private ProductService ps;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listProducts(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("department") @DefaultValue("NONE") final Department department
            ) {
        final List<Product> products = ps.getProductsByCriteria(neighborhoodId, department, page, size);
        final List<ProductDto> productsDto = products.stream()
                .map(p -> ProductDto.fromProduct(p, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/products";
        int totalProductPages = ps.getProductsTotalPages(neighborhoodId, size, department);
        Link[] links = createPaginationLinks(baseUri, page, size, totalProductPages);

        return Response.ok(new GenericEntity<List<ProductDto>>(productsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getById(@PathParam("id") final long id) {
        Optional<Product> product = ps.findProductById(id);
        if (!product.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ProductDto.fromProduct(product.get(), uriInfo)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        ps.deleteProduct(id);
        return Response.noContent().build();
    }
}
