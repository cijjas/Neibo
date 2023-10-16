package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Day;

import java.util.Optional;

public interface DayDao {

    // ------------------------------------------------ DAYS INSERT ----------------------------------------------------

    // Weirdly used, maybe to add some special day like holidays
    Day createDay(String day);

    // ------------------------------------------------ DAYS SELECT ----------------------------------------------------

    Optional<Day> findDayById(long dayId);
}
