package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Shift;

import java.sql.Date;
import java.util.List;

public interface ShiftService {
    List<Shift> getShifts(long amenityId, long dayId, Date date);

}
