package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Shift;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ShiftService {

    Shift createShift(long dayId, long timeId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Shift> findShift(long shiftId);

    Optional<Shift> findShift(long amenityId, long dayId);

    List<Shift> getShifts();

    List<Shift> getShifts(long amenityId);
}
