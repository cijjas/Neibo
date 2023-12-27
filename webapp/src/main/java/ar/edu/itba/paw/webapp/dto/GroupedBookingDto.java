package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.GroupedBooking;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.List;

public class GroupedBookingDto {
    private List<Long> bookingIds;
    private String amenityName;
    private Date date;
    private String day;
    private URI self;
    private URI startTime;
    private URI endTime;

    public static GroupedBookingDto fromGroupedBooking(GroupedBooking groupedBooking, UriInfo uriInfo){
        final GroupedBookingDto dto = new GroupedBookingDto();

//        dto.bookingIds = groupedBooking.getBookingIds();
//        dto.amenityName = groupedBooking.getAmenityName();
//        dto.date = groupedBooking.getDate();
//        dto.day = groupedBooking.getDay();
//
//        dto.self = uriInfo.getBaseUriBuilder()
//                .path("neighborhoods")
//                .path(String.valueOf(groupedBooking.get))
//                .path("users")
//                .path(String.valueOf(booking.getUser().getUserId()))
//                .path("bookings")
//                .path(String.valueOf(booking.getBookingId()))
//                .build();
//        dto.startTime = uriInfo.getBaseUriBuilder()
//                .path("neighborhoods")
//                .path(String.valueOf(booking.getUser().getNeighborhood().getNeighborhoodId()))
//                .path("users")
//                .path(String.valueOf(booking.getUser().getUserId()))
//                .build();
//        dto.endTime = uriInfo.getBaseUriBuilder()
//                .path("neighborhoods")
//                .path(String.valueOf(booking.getUser().getNeighborhood().getNeighborhoodId()))
//                .path("amenities")
//                .path(String.valueOf(booking.getAmenityAvailability().getAmenity().getAmenityId()))
//                .path("availability")
//                .path(String.valueOf(booking.getAmenityAvailability().getAmenityAvailabilityId()))
//                .build();

        return dto;
    }
}
