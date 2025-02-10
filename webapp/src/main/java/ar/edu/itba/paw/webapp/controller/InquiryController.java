package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.InquiryDto;
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
import static ar.edu.itba.paw.webapp.controller.constants.Constant.COUNT_HEADER;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

/*
 * # Summary
 *   - A Product has many Inquiries
 *   - When a User has a question about a certain products it creates an Inquiry for it
 *   - The Product Seller is the only User that can respond the Inquiry
 *
 * # Use cases
 *   - A Neighbor/Admin can create an Inquiry for a Product
 *   - The Seller can respond an Inquiry
 *   - A Neighbor/Admin can list the Inquiries that a certain Product has
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.PRODUCTS + "/{" + PathParameter.PRODUCT_ID + "}/" + Endpoint.INQUIRIES)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class InquiryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InquiryController.class);
    private final InquiryService is;
    private final ProductService ps;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public InquiryController(InquiryService is, ProductService ps) {
        this.is = is;
        this.ps = ps;
    }

    @GET
    public Response listInquiries(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) long productId,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Path Verification
        ps.findProduct(neighborhoodId, productId).orElseThrow(NotFoundException::new);

        // Content
        final List<Inquiry> inquiries = is.getInquiries(productId, size, page);
        String inquiriesHashCode = String.valueOf(inquiries.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(inquiriesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (inquiries.isEmpty())
            return Response.noContent()
                    .tag(inquiriesHashCode)
                    .build();

        final List<InquiryDto> inquiriesDto = inquiries.stream()
                .map(i -> InquiryDto.fromInquiry(i, uriInfo)).collect(Collectors.toList());

        // Pagination Link
        int inquiriesCount = is.countInquiries(productId);
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.INQUIRIES),
                inquiriesCount,
                page,
                size
        );

        return Response.ok(new GenericEntity<List<InquiryDto>>(inquiriesDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(inquiriesHashCode)
                .header(COUNT_HEADER, inquiriesCount)
                .build();
    }

    @GET
    @Path("{" + PathParameter.INQUIRY_ID + "}")
    public Response findInquiry(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) long productId,
            @PathParam(PathParameter.INQUIRY_ID) long inquiryId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Inquiry inquiry = is.findInquiry(neighborhoodId, productId, inquiryId).orElseThrow(NotFoundException::new);
        String inquiryHashCode = String.valueOf(inquiry.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(inquiryHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(InquiryDto.fromInquiry(inquiry, uriInfo))
                .tag(inquiryHashCode)
                .cacheControl(cacheControl)
                .build();
    }

    @POST
    @PreAuthorize("@accessControlHelper.canCreateInquiry(#createForm.user, #neighborhoodId, #productId)")
    @Validated(CreateSequence.class)
    public Response createInquiry(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) long productId,
            @Valid @NotNull InquiryDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Path Verification
        ps.findProduct(neighborhoodId, productId).orElseThrow(NotFoundException::new);

        // Creation & HashCode Generation
        final Inquiry inquiry = is.createInquiry(neighborhoodId, extractFirstId(createForm.getUser()), productId, createForm.getMessage());
        String inquiryHashCode = String.valueOf(inquiry.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(inquiry.getInquiryId())).build();

        return Response.created(uri)
                .tag(inquiryHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.INQUIRY_ID + "}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canUpdateInquiry(#updateForm.user, #neighborhoodId, #productId, #inquiryId)")
    @Validated(UpdateSequence.class)
    public Response updateInquiry(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) long productId,
            @PathParam(PathParameter.INQUIRY_ID) long inquiryId,
            @Valid @NotNull InquiryDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '{}'", uriInfo.getRequestUri());

        // Path Verification
        ps.findProduct(neighborhoodId, productId).orElseThrow(NotFoundException::new);

        // Modification & HashCode Generation
        final Inquiry updatedInquiry = is.replyInquiry(neighborhoodId, productId, inquiryId, updateForm.getReply());
        String inquiryHashCode = String.valueOf(updatedInquiry.hashCode());

        return Response.ok(InquiryDto.fromInquiry(updatedInquiry, uriInfo))
                .tag(inquiryHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.INQUIRY_ID + "}")
    @PreAuthorize("@accessControlHelper.canDeleteInquiry(#neighborhoodId, #productId, #inquiryId)")
    public Response deleteInquiry(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.PRODUCT_ID) long productId,
            @PathParam(PathParameter.INQUIRY_ID) long inquiryId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (is.deleteInquiry(neighborhoodId, productId, inquiryId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
