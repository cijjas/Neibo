package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Reservation;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface ReservationDao {

    // ---------------------------------------------- RESERVATIONS INSERT ----------------------------------------------

    Reservation createReservation(long amenityId, long userId, Date date, Time startTime, Time endTime);

    // ---------------------------------------------- RESERVATIONS SELECT ----------------------------------------------

    Reservation findReservationById(long reservationId);

    List<Reservation> getReservations();

    List<Reservation> getReservationsByAmenityId(long amenityId);

    List<Reservation> getReservationsByUserId(long userId);

    List<Reservation> getReservationsByDay(long amenityId, Date date);

    // ---------------------------------------------- RESERVATIONS DELETE ----------------------------------------------

    boolean deleteReservation(long reservationId);

}
