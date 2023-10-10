package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.DayTime;
import ar.edu.itba.paw.models.Shift;
import enums.StandardTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.channels.SelectableChannel;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AmenityServiceImpl implements AmenityService {
    private final AmenityDao amenityDao;
    private final ShiftDao shiftDao;
    private final AvailabilityDao availabilityDao;

    @Autowired
    public AmenityServiceImpl(final AmenityDao amenityDao, final ShiftDao shiftDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.shiftDao = shiftDao;
        this.amenityDao = amenityDao;
    }

    @Override
    public Amenity createAmenity(String name, String description, Map<String, DayTime> dayHourData, long neighborhoodId){
        return amenityDao.createAmenity(name, description, dayHourData, neighborhoodId);
    }

    @Override
    public List<Amenity> getAmenities(long neighborhoodId) {
        return amenityDao.getAmenities(neighborhoodId);
    }

    @Override
    public Optional<Amenity> findAmenityById(long amenityId) {
        return amenityDao.findAmenityById(amenityId);
    }

    @Override
    public boolean deleteAmenity(long amenityId) {
        return amenityDao.deleteAmenity(amenityId);
    }

    @Override
    public DayTime getAmenityHoursByDay(long amenityId, String dayOfWeek) {
        return amenityDao.getAmenityHoursByDay(amenityId, dayOfWeek);
    }

    @Override
    public Map<String, DayTime> getAmenityHoursByAmenityId(long amenityId) {
        return amenityDao.getAmenityHoursByAmenityId(amenityId);
    }

    @Override
    public Amenity createAmenityWrapper(String name, String description, Time mondayOpenTime, Time mondayCloseTime, Time tuesdayOpenTime, Time tuesdayCloseTime, Time wednesdayOpenTime, Time wednesdayCloseTime, Time thursdayOpenTime, Time thursdayCloseTime, Time fridayOpenTime, Time fridayCloseTime, Time saturdayOpenTime, Time saturdayCloseTime, Time sundayOpenTime, Time sundayCloseTime, long neighborhoodId) {
        Map<String, DayTime> timeMap = new HashMap<>();

        timeMap.put("Monday", createDayTime(mondayOpenTime, mondayCloseTime));
        timeMap.put("Tuesday", createDayTime(tuesdayOpenTime, tuesdayCloseTime));
        timeMap.put("Wednesday", createDayTime(wednesdayOpenTime, wednesdayCloseTime));
        timeMap.put("Thursday", createDayTime(thursdayOpenTime, thursdayCloseTime));
        timeMap.put("Friday", createDayTime(fridayOpenTime, fridayCloseTime));
        timeMap.put("Saturday", createDayTime(saturdayOpenTime, saturdayCloseTime));
        timeMap.put("Sunday", createDayTime(sundayOpenTime, sundayCloseTime));

        System.out.println("Finished filling the map: " + timeMap);

        return createAmenity(name, description, timeMap, neighborhoodId);
    }


    private DayTime createDayTime(Time openTime, Time closeTime) {
        DayTime dayTime = new DayTime();
        dayTime.setOpenTime(openTime);
        dayTime.setCloseTime(closeTime);
        return dayTime;
    }

    @Override
    public List<Time> getAllTimes() {
        List<Time> timeList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            Date startTime = sdf.parse("00:00");
            Date endTime = sdf.parse("23:30");

            long currentTime = startTime.getTime();
            long endTimeMillis = endTime.getTime();

            while (currentTime <= endTimeMillis) {
                timeList.add(new Time(currentTime));
                currentTime += 30 * 60 * 1000; // Add 30 minutes in milliseconds USE AN ENUM PLS
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeList;
    }

    @Override
    public Amenity createAmenity(String name, String description, long neighborhoodId, List<String> selectedShifts) {
        Amenity amenity = amenityDao.createAmenity(name, description, neighborhoodId);

        for (String shiftPair : selectedShifts) {
            // Parse the pair into <Long dayId, Long timeId>
            String[] shiftParts = shiftPair.split(",");
            if (shiftParts.length != 2) {
                // log error
            }

            long dayId = Long.parseLong(shiftParts[0]);
            long timeId = Long.parseLong(shiftParts[1]);

            Optional<Shift> existingShift = shiftDao.findShift(dayId, timeId);

            if (existingShift.isPresent()) {
                availabilityDao.createAvailability(amenity.getAmenityId(), existingShift.get().getShiftId());
            } else {
                // Shift doesn't exist, create a new shift and then create an availability entry
                Shift newShift = shiftDao.createShift(dayId, timeId);
                availabilityDao.createAvailability(amenity.getAmenityId(), newShift.getShiftId());
            }
        }

        return amenity;
    }




    /*
    Se usa asi:

    AmenityDao amenityDao; // Inject or create an instance of AmenityDao

    long amenityId = 1; // Replace with the desired amenity ID
    String dayOfWeek = "Monday"; // Replace with the desired day of the week

    Map<Time, Time> openingClosingHours = amenityDao.getAmenityByDay(amenityId, dayOfWeek);

    // Now you can use the openingClosingHours map as needed in your application.
*/

//    @Override
//    public boolean updateAmenity(long amenityId, String name, String description) {
//        return amenityDao.updateAmenity(amenityId, name, description);
//    }
}
