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
        Review review = rs.findReview(id, workerId).orElseThrow(NotFoundException::new);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        EntityTag entityTag = new EntityTag(review.getReviewId().toString());
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

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .tag(entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final Review review = rs.createReview(workerId, getLoggedUserId(), form.getRating(), form.getReview());
        entityLevelETag = ETagUtility.generateETag();

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(review.getReviewId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .build();
    }
}
