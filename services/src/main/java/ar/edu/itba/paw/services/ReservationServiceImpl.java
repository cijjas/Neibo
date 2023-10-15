package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.ReservationDao;
import ar.edu.itba.paw.interfaces.services.ReservationService;
import ar.edu.itba.paw.models.DayTime;
import ar.edu.itba.paw.models.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationDao reservationDao;
    private final AmenityDao amenityDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);

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

//    @Override
//    public List<DayTime> getAvailableTimesByDate(long amenityId, Date date) {
//        // Get the opening and closing times for the amenity on the specified day of the week
//        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
//        String dayOfWeek = dateFormat.format(date);
//        DayTime amenityHours = amenityDao.getAmenityHoursByDay(amenityId, dayOfWeek);
//
//        // Initialize a list of available time slots with the full opening-closing time
//        List<DayTime> availableTimeSlots = new ArrayList<>();
//        availableTimeSlots.add(amenityHours);
//
//        // Query the database to retrieve reservations for the amenity on the specified date
//        List<Reservation> reservations = reservationDao.getReservationsByDay(amenityId, date);
//
//        // Iterate through reservations and adjust available time slots
//        for (Reservation reservation : reservations) {
//            DayTime reservationTimeSlot = new DayTime();
//            reservationTimeSlot.setOpenTime(reservation.getStartTime());
//            reservationTimeSlot.setCloseTime(reservation.getEndTime());
//
//            // Iterate through available time slots and adjust for overlapping reservations
//            List<DayTime> newAvailableTimeSlots = new ArrayList<>();
//            for (DayTime availableTimeSlot : availableTimeSlots) {
//                List<DayTime> splitTimeSlots = splitTimeSlot(availableTimeSlot, reservationTimeSlot);
//                newAvailableTimeSlots.addAll(splitTimeSlots);
//            }
//
//            availableTimeSlots = newAvailableTimeSlots;
//        }
//
//        return availableTimeSlots;
//    }

    // Split a time slot into two parts, excluding the provided time slot
//    private List<DayTime> splitTimeSlot(DayTime toSplit, DayTime excludedTimeSlot) {
//        List<DayTime> result = new ArrayList<>();
//
//        // Check if the excluded time slot is entirely before this time slot
//        if (excludedTimeSlot.getCloseTime().compareTo(toSplit.getOpenTime()) <= 0) {
//            result.add(toSplit);
//        }
//        // Check if the excluded time slot is entirely after this time slot
//        else if (excludedTimeSlot.getOpenTime().compareTo(toSplit.getCloseTime()) >= 0) {
//            result.add(toSplit);
//        }
//        // Otherwise, split this time slot into two parts
//        else {
//            // Create the first part before the excluded time slot
//            if (excludedTimeSlot.getOpenTime().compareTo(toSplit.getOpenTime()) > 0) {
//                DayTime firstPart = new DayTime();
//                firstPart.setOpenTime(toSplit.getOpenTime());
//                firstPart.setCloseTime(excludedTimeSlot.getOpenTime());
//                result.add(firstPart);
//            }
//            // Create the second part after the excluded time slot
//            if (excludedTimeSlot.getCloseTime().compareTo(toSplit.getCloseTime()) < 0) {
//                DayTime secondPart = new DayTime();
//                secondPart.setOpenTime(excludedTimeSlot.getCloseTime());
//                secondPart.setCloseTime(toSplit.getCloseTime());
//                result.add(secondPart);
//            }
//        }
//
//        return result;
//    }

    @Override
    public List<String> getDaysOfWeek() {
        List<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");

        return daysOfWeek;
    }


    @Override
    public List<Time> getAvailableTimesByDate(long amenityId, Date date) {
        //get day of week of date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        String dayOfWeek = dateFormat.format(date);
        DayTime amenityHours = amenityDao.getAmenityHoursByDay(amenityId, dayOfWeek);

        List<Time> timeList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Time startTime = amenityHours.getOpenTime();
        Time endTime = amenityHours.getCloseTime();

        long currentTime = startTime.getTime();
        long endTimeMillis = endTime.getTime();

        while (currentTime <= endTimeMillis) {
            //if reservation arranca en currentTime, saltar a endTime de reservation
            timeList.add(new Time(currentTime));
            currentTime += 30 * 60 * 1000; // Add 30 minutes in milliseconds
        }

        return timeList;
    }

//    public List<DayTime> getAvailableTimesByDate(Date date, long amenityId) {
//        // Get the opening and closing times for the amenity on the specified day of the week
//        DayOfWeek dayOfWeek = date.toLocalDate().getDayOfWeek();
//        DayTime amenityHours = amenityDao.getAmenityHoursByDay(amenityId, dayOfWeek.name());
//
//        Time startTime = amenityHours.getOpenTime();
//        Time endTime = amenityHours.getCloseTime();
//
//        // Initialize a list of available time slots with the full opening-closing time
//        List<DayTime> availableTimeSlots = new ArrayList<>();
//        DayTime timeSlot = new DayTime();
//        timeSlot.setOpenTime(startTime);
//        timeSlot.setCloseTime(endTime);
//        availableTimeSlots.add(timeSlot);
//
//        // Query the database to retrieve reservations for the amenity on the specified date
//        List<Reservation> reservations = getReservationsByDay(amenityId, date);
//
//        // Iterate through reservations and adjust available time slots
//        for (Reservation reservation : reservations) {
//            DayTime reservationTimeSlot = new DayTime();
//            reservationTimeSlot.setOpenTime(reservation.getStartTime());
//            reservationTimeSlot.setCloseTime(reservation.getEndTime());
//
//            // Iterate through available time slots and adjust for overlapping reservations
//            List<DayTime> newAvailableTimeSlots = new ArrayList<>();
//            for (DayTime availableTimeSlot : availableTimeSlots) {
//                List<DayTime> splitTimeSlots = availableTimeSlot.split(reservationTimeSlot);
//                newAvailableTimeSlots.addAll(splitTimeSlots);
//            }
//
//            availableTimeSlots = newAvailableTimeSlots;
//        }
//
//        return availableTimeSlots;
//    }
//
//    private List<DayTime> splitTimeSlot(DayTime toSplit, DayTime excludedTimeSlot) {
//        List<DayTime> result = new ArrayList<>();
//
//        // Check if the excluded time slot is entirely before this time slot
//        if (excludedTimeSlot.getCloseTime().compareTo(toSplit.getOpenTime()) <= 0) {
//            result.add(new TimeSlot(startTime, endTime));
//        }
//        // Check if the excluded time slot is entirely after this time slot
//        else if (excludedTimeSlot.getStartTime().compareTo(endTime) >= 0) {
//            result.add(new TimeSlot(startTime, endTime));
//        }
//        // Otherwise, split this time slot into two parts
//        else {
//            // Create the first part before the excluded time slot
//            if (excludedTimeSlot.getStartTime().compareTo(startTime) > 0) {
//                result.add(new TimeSlot(startTime, excludedTimeSlot.getStartTime()));
//            }
//            // Create the second part after the excluded time slot
//            if (excludedTimeSlot.getEndTime().compareTo(endTime) < 0) {
//                result.add(new TimeSlot(excludedTimeSlot.getEndTime(), endTime));
//            }
//        }
//
//        return result;
//    }
//
//
//    @Override
//    public List<Time> getAvailableTimesByDate(long amenityId, Date date) {
//        //get day of week of date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
//        String dayOfWeek = dateFormat.format(date);
//        DayTime amenityHours = amenityDao.getAmenityHoursByDay(amenityId, dayOfWeek);
//
//        List<Time> timeList = new ArrayList<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//
//        try {
////            java.util.Date startTime = amenityHours.getOpenTime();
////            java.util.Date endTime = amenityHours.getCloseTime();
//            Time startTime = amenityHours.getOpenTime();
//            Time endTime = amenityHours.getCloseTime();
//
//            long currentTime = startTime.getTime();
//            long endTimeMillis = endTime.getTime();
//
//            while (currentTime <= endTimeMillis) {
//                //if reservation arranca en currentTime, saltar a endTime de reservation
//                if(currentTime ==)
//                timeList.add(new Time(currentTime));
//                currentTime += 30 * 60 * 1000; // Add 30 minutes in milliseconds
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }

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
