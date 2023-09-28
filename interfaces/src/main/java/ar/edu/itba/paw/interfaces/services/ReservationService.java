package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Reservation;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface ReservationService {
    Reservation createReservation(long amenityId, long userId, Date date, Time startTime, Time endTime);

    List<Reservation> getReservations();

    List<Reservation> getReservationsByAmenityId(long amenityId);

    List<Reservation> getReservationsByUserId(long userId);

    List<Reservation> getReservationsByDay(long amenityId, Date date);

    Reservation findReservationById(long reservationId);

    boolean deleteReservation(long reservationId);

}
