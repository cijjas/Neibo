package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
@Path("workers/{workerId}/reviews")
@Component
public class ReviewController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private ReviewService rs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("workerId")
    private Long workerId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @Autowired
    public ReviewController(final UserService us) {
        super(us);
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listReviews(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/workers/{}/reviews'", workerId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Review> reviews = rs.getReviews(workerId, page, size);
        if (reviews.isEmpty())
            return Response.noContent().build();
        final List<ReviewDto> reviewsDto = reviews.stream()
                .map(r -> ReviewDto.fromReview(r, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "workers/" + workerId + "/reviews",
                rs.calculateReviewPages(workerId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ReviewDto>>(reviewsDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findReview(
            @PathParam("id") final long id
    ) {
        LOGGER.info("GET request arrived at '/workers/{}/reviews/{}'", workerId, id);

        // Content
        Review review = rs.findReview(id, workerId).orElseThrow(() -> new NotFoundException("Review Not Found"));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(review.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ReviewDto.fromReview(review, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_NEIGHBOR"})
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createReview(
            @Valid final ReviewForm form
    ) {
        LOGGER.info("POST request arrived at '/workers/{}/reviews'", workerId);

        // Check If-Match Header
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Usual Flow
        final Review review = rs.createReview(workerId, getLoggedUser().getUserId(), form.getRating(), form.getReview());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(review.getReviewId())).build();
        entityLevelETag = ETagUtility.generateETag();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }
}
