package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.dto.ReviewsAverageDto;
import ar.edu.itba.paw.webapp.dto.ReviewsCountDto;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
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
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

/*
 * # Summary
 *   - A Worker has many Reviews
 *
 * # Use cases
 *   - A Neighbor can leave a Review for a certain Worker
 *   - A Neighbor/Admin/Worker can list the Reviews of the Workers and their overall score
 */

@Path(Endpoint.API + "/" + Endpoint.WORKERS + "/{" + PathParameter.WORKER_ID + "}/" + Endpoint.REVIEWS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class ReviewController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);
    private final ReviewService rs;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public ReviewController(ReviewService rs) {
        this.rs = rs;
    }

    @GET
    public Response listReviews(
            @PathParam(PathParameter.WORKER_ID) long workerId,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        final List<Review> reviews = rs.getReviews(workerId, page, size);
        String reviewsHashCode = String.valueOf(reviews.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(reviewsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (reviews.isEmpty())
            return Response.noContent()
                    .tag(reviewsHashCode)
                    .build();

        final List<ReviewDto> reviewsDto = reviews.stream()
                .map(r -> ReviewDto.fromReview(r, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKERS).path(String.valueOf(workerId)).path(Endpoint.REVIEWS),
                rs.calculateReviewPages(workerId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ReviewDto>>(reviewsDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(reviewsHashCode)
                .build();
    }

    @GET
    @Path(Endpoint.COUNT)
    public Response countReviews(
            @PathParam(PathParameter.WORKER_ID) long workerId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        int count = rs.countReviews(workerId);
        String countHashCode = String.valueOf(count);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(countHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        ReviewsCountDto dto = ReviewsCountDto.fromReviewsCount(count, workerId, uriInfo);

        return Response.ok(new GenericEntity<ReviewsCountDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(countHashCode)
                .build();
    }

    @GET
    @Path(Endpoint.AVERAGE)
    public Response averageReviews(
            @PathParam(PathParameter.WORKER_ID) long workerId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        float average = rs.findAverageRating(workerId);
        String averageHashCode = String.valueOf(average);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(averageHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        ReviewsAverageDto dto = ReviewsAverageDto.fromReviewAverage(average, workerId, uriInfo);

        return Response.ok(new GenericEntity<ReviewsAverageDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(averageHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.REVIEW_ID + "}")
    public Response findReview(
            @PathParam(PathParameter.WORKER_ID) long workerId,
            @PathParam(PathParameter.REVIEW_ID) long reviewId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Review review = rs.findReview(workerId, reviewId).orElseThrow(NotFoundException::new);
        String reviewHashCode = String.valueOf(review.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(reviewHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ReviewDto.fromReview(review, uriInfo))
                .cacheControl(cacheControl)
                .tag(reviewHashCode)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateReview(#createForm.user, #workerId)")
    public Response createReview(
            @PathParam(PathParameter.WORKER_ID) long workerId,
            @Valid @NotNull ReviewDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        final Review review = rs.createReview(workerId, extractFirstId(createForm.getUser()), createForm.getRating(), createForm.getMessage());
        String reviewHashCode = String.valueOf(review.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(review.getReviewId())).build();

        return Response.created(uri)
                .tag(reviewHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.REVIEW_ID + "}")
    @PreAuthorize("@accessControlHelper.canDeleteReview(#workerId, #reviewId)")
    public Response deleteReview(
            @PathParam(PathParameter.WORKER_ID) long workerId,
            @PathParam(PathParameter.REVIEW_ID) long reviewId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (rs.deleteReview(workerId, reviewId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
