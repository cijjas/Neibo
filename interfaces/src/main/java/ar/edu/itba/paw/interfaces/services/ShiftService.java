package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Shift;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ShiftService {

    Optional<Shift> findShift(long shiftId);

    List<Shift> getShifts(String amenityId, Date date);
}
