package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.Shift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class AmenityServiceImpl implements AmenityService {
    private final AmenityDao amenityDao;
    private final ShiftDao shiftDao;
    private final AvailabilityDao availabilityDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityServiceImpl.class);

    @Autowired
    public AmenityServiceImpl(final AmenityDao amenityDao, final ShiftDao shiftDao, final AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
        this.shiftDao = shiftDao;
        this.amenityDao = amenityDao;
    }

<<<<<<< HEAD
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, Map<String, DayTime> dayHourData, long neighborhoodId){
        return amenityDao.createAmenity(name, description, dayHourData, neighborhoodId);
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
    public Map<Amenity, List<Shift>> getAllAmenitiesIdWithListOfShifts(long neighborhoodId) {
        List<Amenity> amenityList = amenityDao.getAmenities(neighborhoodId);

        Map<Amenity, List<Shift>> amenityShifts = new HashMap<>();
        for (Amenity amenity : amenityList) {
            List<Shift> shiftList = shiftDao.getAmenityShifts(amenity.getAmenityId());
            amenityShifts.put(amenity, shiftList);
        }
        return amenityShifts;
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
=======
>>>>>>> de64f2dd997be1d9dbc6bd76831340a025493792

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Amenity createAmenity(String name, String description, long neighborhoodId, List<String> selectedShifts) {
        LOGGER.info("Creating Amenity {}", name);
        Amenity amenity = amenityDao.createAmenity(name, description, neighborhoodId);

        for (String shiftPair : selectedShifts) {
            // Parse the pair into <Long dayId, Long timeId>
            String[] shiftParts = shiftPair.split(",");

            long dayId = Long.parseLong(shiftParts[0]);
            long timeId = Long.parseLong(shiftParts[1]);

            Optional<Shift> existingShift = shiftDao.findShiftId(timeId, dayId);

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

    @Override
    public Optional<Amenity> findAmenityById(long amenityId) {
        return amenityDao.findAmenityById(amenityId);
    }

    @Override
    public List<Amenity> getAmenities(long neighborhoodId) {
        LOGGER.info("Getting Amenities from Neighborhood {}", neighborhoodId);
        return amenityDao.getAmenities(neighborhoodId);
    }

    @Override
    public boolean deleteAmenity(long amenityId) {
        LOGGER.info("Deleting Amenity {}", amenityId);
        return amenityDao.deleteAmenity(amenityId);
    }

}
