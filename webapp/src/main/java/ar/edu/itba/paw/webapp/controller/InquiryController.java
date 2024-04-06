package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.webapp.dto.InquiryDto;
import ar.edu.itba.paw.webapp.form.InquiryForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.*;

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
            @PathParam("id") final long inquiryId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/products/{}/inquiries/{}'", neighborhoodId, productId, inquiryId);

        // Content
        Inquiry inquiry = is.findInquiry(inquiryId, productId, neighborhoodId).orElseThrow(NotFoundException::new);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(inquiry.getVersion().toString());
        Response response = checkMutableETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        return Response.ok(InquiryDto.fromInquiry(inquiry, uriInfo))
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canCreateInquiry(#productId)")
    public Response createInquiry(
            @Valid @NotNull final InquiryForm form,
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
        EntityTag rowLevelETag = new EntityTag(inquiry.getVersion().toString());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(inquiry.getInquiryId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAnswerInquiry(#inquiryId)")
    public Response updateInquiry(
            @PathParam("id") final long inquiryId,
            @Valid @NotNull final InquiryForm form,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/products/{}/inquiries/{}'", neighborhoodId, productId, inquiryId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(is.findInquiry(inquiryId, productId, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString());
        Response response = checkModificationETagPreconditions(ifMatch, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Modification & ETag Generation
        final Inquiry updatedInquiry = is.replyInquiry(inquiryId, form.getQuestionMessage());
        entityLevelETag = ETagUtility.generateETag();
        rowLevelETag = new EntityTag(updatedInquiry.getVersion().toString());

        return Response.ok(InquiryDto.fromInquiry(updatedInquiry, uriInfo))
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canDeleteInquiry(#inquiryId)")
    public Response deleteById(
            @PathParam("id") final long inquiryId,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/products/{}/inquiries/{}'", neighborhoodId, productId, inquiryId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(is.findInquiry(inquiryId).orElseThrow(NotFoundException::new).getVersion().toString());
        Response response = checkModificationETagPreconditions(ifMatch, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Deletion & ETag Generation Attempt
        if (is.deleteInquiry(inquiryId)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

}
