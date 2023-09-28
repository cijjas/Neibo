package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.Amenity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AmenityServiceImpl implements AmenityService {
    private final AmenityDao amenityDao;

    @Autowired
    public AmenityServiceImpl(final AmenityDao amenityDao) {
        this.amenityDao = amenityDao;
    }

    @Override
    public Amenity createAmenity(String name, String description, Map<String, Map<Time, Time>> dayHourData){
        return amenityDao.createAmenity(name, description, dayHourData);
    }

    @Override
    public List<Amenity> getAmenities() {
        return amenityDao.getAmenities();
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
    public Map<Time, Time> getAmenityHoursByDay(long amenityId, String dayOfWeek) {
        return amenityDao.getAmenityHoursByDay(amenityId, dayOfWeek);
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
