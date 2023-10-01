package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.ReservationDao;
import ar.edu.itba.paw.interfaces.services.ReservationService;
import ar.edu.itba.paw.models.DayTime;
import ar.edu.itba.paw.models.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationDao reservationDao;
    private final AmenityDao amenityDao;

    @Autowired
    public ReservationServiceImpl(final ReservationDao reservationDao, AmenityDao amenityDao) {
        this.reservationDao = reservationDao;
        this.amenityDao = amenityDao;
    }

    @Override
    public Reservation createReservation(long amenityId, long userId, Date date, Time startTime, Time endTime, long neighborhoodId) {
        //startTime > endTime || amenity not open || theres another reservation during request time
        if(startTime.after(endTime) || !isAmenityOpen(date, startTime, endTime, amenityId, neighborhoodId) || reservationOverlap(date, startTime, endTime, reservationDao.getReservationsByDay(amenityId, date))) {
            return null;
        }

        return reservationDao.createReservation(amenityId, userId, date, startTime, endTime);
    }

    private boolean isAmenityOpen(Date date, Time startTime, Time endTime, long amenityId, long neighborhoodId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        String dayOfWeek = dateFormat.format(date);
        DayTime amenityHours = amenityDao.getAmenityHoursByDay(amenityId, dayOfWeek);

        if (amenityHours != null) {
            Time openTime = amenityHours.getOpenTime();
            Time closeTime = amenityHours.getCloseTime();

            return startTime.compareTo(openTime) >= 0 && endTime.compareTo(closeTime) <= 0; // true if Amenity is open at the requested time
        }

        return false; // Amenity is not open at the requested time
    }

    private boolean reservationOverlap(Date date, Time startTime, Time endTime, List<Reservation> reservations) {
        for(Reservation reservation : reservations) {
            Time existingStartTime = reservation.getStartTime();
            Time existingEndTime = reservation.getEndTime();

            //wants to start in the middle of another reservation
            if (startTime.compareTo(existingStartTime) >= 0 && startTime.compareTo(existingEndTime) < 0) {
                return true; // There is an overlap with an existing reservation
            }

            //starts before but ends in the middle of another reservation
            if (endTime.compareTo(existingStartTime) > 0 && endTime.compareTo(existingEndTime) <= 0) {
                return true; // There is an overlap with an existing reservation
            }

            //starts before and ends after another reservation
            if (startTime.compareTo(existingStartTime) <= 0 && endTime.compareTo(existingEndTime) >= 0) {
                return true; // There is an overlap with an existing reservation
            }
        }
        return false; //no overlap!
    }

    @Override
    public List<Reservation> getReservations() {
        return reservationDao.getReservations();
    }

    @Override
    public Reservation findReservationById(long reservationId) {
        return reservationDao.findReservationById(reservationId);
    }

    @Override
    public List<Reservation> getReservationsByAmenityId(long amenityId) {
        return reservationDao.getReservationsByAmenityId(amenityId);
    }

    @Override
    public List<Reservation> getReservationsByUserId(long userId) {
        return reservationDao.getReservationsByUserId(userId);
    }

    @Override
    public List<Reservation> getReservationsByDay(long amenityId, Date date){
        return reservationDao.getReservationsByDay(amenityId, date);
    }

    @Override
    public boolean deleteReservation(long reservationId) {
        return reservationDao.deleteReservation(reservationId);
    }

}
