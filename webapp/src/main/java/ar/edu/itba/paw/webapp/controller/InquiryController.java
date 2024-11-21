package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.webapp.dto.InquiryDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
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
import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractSecondId;

/*
 * # Summary
 *   - A Product has many Inquiries
 *   - When a User has a question about a certain products it creates an Inquiry for it
 *   - The Product Seller is the only User that can respond the Inquiry
 *
 * # Use cases
 *   - A User/Admin can create an Inquiry for a Product
 *   - The Seller can respond an Inquiry
 *   - A User/Admin can list the Inquiries that a certain Product has
 */

@Path("neighborhoods/{neighborhoodId}/products/{productId}/inquiries")
@Component
@Validated
public class InquiryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InquiryController.class);

    @Autowired
    private InquiryService is;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listInquiries(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("productId") @NeighborhoodIdConstraint final Long productId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/products/{}/inquiries'", neighborhoodId, productId);

        // Content
        final List<Inquiry> inquiries = is.getInquiries(productId, page, size, neighborhoodId);
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
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/products" + productId + "/inquiries",
                is.calculateInquiryPages(productId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<InquiryDto>>(inquiriesDto) {})
                .links(links)
                .cacheControl(cacheControl)
                .tag(inquiriesHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findInquiry(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("productId") @NeighborhoodIdConstraint final Long productId,
            @PathParam("id") @GenericIdConstraint final long inquiryId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/products/{}/inquiries/{}'", neighborhoodId, productId, inquiryId);

        // Content
        Inquiry inquiry = is.findInquiry(inquiryId, productId, neighborhoodId).orElseThrow(() -> new NotFoundException("Inquiry not found"));
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
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canCreateInquiry(#productId)")
    @Validated(CreateValidationSequence.class)
    public Response createInquiry(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("productId") @NeighborhoodIdConstraint final Long productId,
            @Valid InquiryDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/products/{}/inquiries'", neighborhoodId, productId);

        // Creation & HashCode Generation
        final Inquiry inquiry = is.createInquiry(extractSecondId(form.getUser()), productId, form.getQuestionMessage());
        String inquiryHashCode = String.valueOf(inquiry.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(inquiry.getInquiryId())).build();

        return Response.created(uri)
                .tag(inquiryHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canAnswerInquiry(#inquiryId)")
    @Validated(UpdateValidationSequence.class)
    public Response updateInquiry(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("productId") @NeighborhoodIdConstraint final Long productId,
            @PathParam("id") @GenericIdConstraint final long inquiryId,
            @Valid InquiryDto form
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/products/{}/inquiries/{}'", neighborhoodId, productId, inquiryId);

        // Modification & HashCode Generation
        final Inquiry updatedInquiry = is.replyInquiry(inquiryId, form.getReplyMessage());
        String inquiryHashCode = String.valueOf(updatedInquiry.hashCode());

        return Response.ok(InquiryDto.fromInquiry(updatedInquiry, uriInfo))
                .tag(inquiryHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canDeleteInquiry(#inquiryId)")
    public Response deleteById(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("productId") @NeighborhoodIdConstraint final Long productId,
            @PathParam("id") @GenericIdConstraint final long inquiryId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/products/{}/inquiries/{}'", neighborhoodId, productId, inquiryId);

        // Deletion Attempt
        if (is.deleteInquiry(inquiryId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
