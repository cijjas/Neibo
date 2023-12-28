package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
@Path("workers/{workerId}/reviews")
@Component
public class ReviewController {

    @Autowired
    private ReviewService rs;

    @Context
    private UriInfo uriInfo;

    @PathParam("workerId")
    private Long workerId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listReviews(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size) {
        final List<Review> reviews = rs.getReviews(workerId, page, size);
        final List<ReviewDto> reviewsDto = reviews.stream()
                .map(r -> ReviewDto.fromReview(r, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "workers/" + workerId + "/reviews";
        int totalReviewPages = rs.getReviewsTotalPages(workerId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalReviewPages);

        return Response.ok(new GenericEntity<List<ReviewDto>>(reviewsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findReview(@PathParam("id") final long id) {
        Optional<Review> review = rs.getReview(id);
        if (!review.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ReviewDto.fromReview(review.get(), uriInfo)).build();
    }

}
