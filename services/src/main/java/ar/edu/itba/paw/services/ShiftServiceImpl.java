package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.models.Shift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftDao shiftDao;

    @Autowired
    public ShiftServiceImpl(final ShiftDao shiftDao) {
        this.shiftDao = shiftDao;
    }

    @Override
    public List<Shift> getShifts(long amenityId, long dayId, Date date) {
        return shiftDao.getShifts(amenityId, dayId, date);
    }
}
