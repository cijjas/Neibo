package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.InquiryDto;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.form.ListingForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
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

@Path("neighborhoods/{neighborhoodId}/products/{productId}/inquiries")
@Component
public class InquiryController extends GlobalControllerAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(InquiryController.class);

    private final InquiryService is;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("productId")
    private Long productId;

    @Autowired
    public InquiryController(final UserService us, final InquiryService is) {
        super(us);
        this.is = is;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listInquiries(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size) {
        LOGGER.info("GET request arrived at neighborhoods/{}/products/{}/inquiries", neighborhoodId, productId);
        final List<Inquiry> inquiries = is.getInquiriesByProductAndCriteria(productId, page, size);
        final List<InquiryDto> inquiriesDto = inquiries.stream()
                .map(i -> InquiryDto.fromInquiry(i, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/products" + productId + "inquiries";
        int totalInquiryPages = is.getTotalInquiryPages(productId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalInquiryPages);

        return Response.ok(new GenericEntity<List<InquiryDto>>(inquiriesDto){})
                .links(links)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createInquiry(@Valid final QuestionForm form) {
        LOGGER.info("POST request arrived at neighborhoods/{}/products/{}/inquiries", neighborhoodId, productId);
        final Inquiry inquiry = is.createInquiry(getLoggedUser().getUserId(), productId, form.getQuestionMessage());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(inquiry.getInquiryId())).build();
        return Response.created(uri).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response answerInquiry(@PathParam("id") final long id, @Valid final QuestionForm form) {
        LOGGER.info("PATCH request arrived at neighborhoods/{}/products/{}/inquiries/{}", neighborhoodId, productId, id);
        final Inquiry inquiry = is.replyInquiry(id, form.getQuestionMessage());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(inquiry.getInquiryId())).build();
        return Response.created(uri).build();
    }

}
