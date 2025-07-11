package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.BookingService;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import ar.edu.itba.paw.webapp.dto.queryForms.BookingParams;
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
import static ar.edu.itba.paw.webapp.controller.constants.Constant.COUNT_HEADER;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.*;

/*
 * # Summary
 *   - A Booking is the relationship between a User and an Availability, it also adds a date attribute to the relationship
 *
 * # Use cases
 *   - A Neighbor/Admin can create/delete a Booking
 *   - A Neighbor/Admin can list his Bookings
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.BOOKINGS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class BookingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bs;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public BookingController(BookingService bs) {
        this.bs = bs;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canListBookings(#bookingParams.user, #bookingParams.amenity)")
    public Response listBookings(
            @Valid @BeanParam BookingParams bookingParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long userId = extractNullableFirstId(bookingParams.getUser());
        Long amenityId = extractNullableSecondId(bookingParams.getAmenity());

        // Content
        final List<Booking> bookings = bs.getBookings(bookingParams.getNeighborhoodId(), userId, amenityId, bookingParams.getPage(), bookingParams.getSize());
        String bookingsHashCode = String.valueOf(bookings.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(bookingsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (bookings.isEmpty())
            return Response.noContent()
                    .tag(bookingsHashCode)
                    .build();

        final List<BookingDto> bookingsDto = bookings.stream()
                .map(b -> BookingDto.fromBooking(b, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        int bookingsCount = bs.countBookings(bookingParams.getNeighborhoodId(), userId, amenityId);
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(bookingParams.getNeighborhoodId())).path(Endpoint.BOOKINGS),
                bookingsCount,
                bookingParams.getPage(),
                bookingParams.getSize()
        );

        return Response.ok(new GenericEntity<List<BookingDto>>(bookingsDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(bookingsHashCode)
                .header(COUNT_HEADER, bookingsCount)
                .build();
    }

    @GET
    @Path("{" + PathParameter.BOOKING_ID + "}")
    public Response findBooking(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.BOOKING_ID) long bookingId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        Booking booking = bs.findBooking(neighborhoodId, bookingId).orElseThrow(NotFoundException::new);
        String bookingHashCode = String.valueOf(booking.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(bookingHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        BookingDto bookingDto = BookingDto.fromBooking(booking, uriInfo);

        return Response.ok(bookingDto)
                .tag(bookingHashCode)
                .cacheControl(cacheControl)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateBooking(#createForm.user, #createForm.amenity, #createForm.shift)")
    public Response createBooking(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @NotNull BookingDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        final Booking booking = bs.createBooking(extractFirstId(createForm.getShift()), extractFirstId(createForm.getUser()), extractSecondId(createForm.getAmenity()), extractDate(createForm.getBookingDate()));
        String bookingHashCode = String.valueOf(booking.hashCode());

        // Resource URI
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(booking.getBookingId())).build();

        return Response.created(uri)
                .tag(bookingHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.BOOKING_ID + "}")
    @PreAuthorize("@accessControlHelper.canDeleteBooking(#bookingId, #neighborhoodId)")
    public Response deleteBooking(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.BOOKING_ID) long bookingId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (bs.deleteBooking(neighborhoodId, bookingId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
