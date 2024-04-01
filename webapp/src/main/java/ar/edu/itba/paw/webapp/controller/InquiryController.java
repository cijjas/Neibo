package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.InquiryDto;
import ar.edu.itba.paw.webapp.form.QuestionForm;
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

@Path("neighborhoods/{neighborhoodId}/products/{productId}/inquiries")
@Component
public class InquiryController extends GlobalControllerAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(InquiryController.class);

    @Autowired
    private InquiryService is;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("productId")
    private Long productId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listInquiries(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/products/{}/inquiries'", neighborhoodId, productId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Inquiry> inquiries = is.getInquiries(productId, page, size, neighborhoodId);
        if (inquiries.isEmpty())
            return Response.noContent().build();
        final List<InquiryDto> inquiriesDto = inquiries.stream()
                .map(i -> InquiryDto.fromInquiry(i, uriInfo)).collect(Collectors.toList());

        // Pagination Link
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/products" + productId + "inquiries",
                is.calculateInquiryPages(productId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<InquiryDto>>(inquiriesDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findInquiry(
            @PathParam("id") final long inquiryId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/products/{}/inquiries/{}'", neighborhoodId, productId, inquiryId);

        // Content
        Inquiry inquiry = is.findInquiry(inquiryId, productId, neighborhoodId).orElseThrow(NotFoundException::new);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(inquiry.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(InquiryDto.fromInquiry(inquiry, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canCreateInquiry(#productId)")
    public Response createInquiry(
            @Valid final QuestionForm form,
            @PathParam("productId") final long productId
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/products/{}/inquiries'", neighborhoodId, productId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .tag(entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final Inquiry inquiry = is.createInquiry(getLoggedUserId(), productId, form.getQuestionMessage());
        entityLevelETag = ETagUtility.generateETag();

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(inquiry.getInquiryId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAnswerInquiry(#inquiryId)")
    public Response updateInquiry(
            @PathParam("id") final long inquiryId,
            @Valid final QuestionForm form,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/products/{}/inquiries/{}'", neighborhoodId, productId, inquiryId);

        // Cache Control
        if (ifMatch != null){
            String rowVersion = is.findInquiry(inquiryId, productId, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(rowVersion));
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .tag(rowVersion)
                        .build();
        }

        // Modification & ETag Generation
        final InquiryDto inquiryDto = InquiryDto.fromInquiry(is.replyInquiry(inquiryId, form.getQuestionMessage()), uriInfo);
        entityLevelETag = ETagUtility.generateETag();

        return Response.ok(inquiryDto)
                .tag(entityLevelETag)
                .build();
    }

}
