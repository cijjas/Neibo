package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Shift;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ShiftService {
    Optional<Shift> findShift(long amenityId, long dayId);

    List<Shift> getShifts(long amenityId, long dayId, Date date);

    Shift createShift(long dayId, long timeId);

}
