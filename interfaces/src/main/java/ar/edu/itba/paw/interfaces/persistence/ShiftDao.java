package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Shift;
import enums.DayOfTheWeek;
import enums.StandardTime;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ShiftDao {
    Shift createShift(long day, long startTimeId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Shift> findShiftById(long shiftId);

    Optional<Shift> findShiftId(long startTime, long dayId);

    List<Shift> getShifts(long amenityId, long dayId, Date date);
}
