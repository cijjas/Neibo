package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Shift;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ShiftDao {

    // ----------------------------------------------- SHIFTS INSERT ---------------------------------------------------

    Shift createShift(long day, long startTimeId);

    // ----------------------------------------------- SHIFTS SELECT ---------------------------------------------------

    Optional<Shift> findShiftById(long shiftId);

    Optional<Shift> findShiftId(long startTime, long dayId);

    List<Shift> getShifts(long amenityId, long dayId, Date date);

    List<Shift> getAmenityShifts(long amenityId);
}
