package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.MainEntities.Day;

import java.util.Optional;

public interface DayDao {

    // ------------------------------------------------ DAYS INSERT ----------------------------------------------------

    Day createDay(String day);

    // ------------------------------------------------ DAYS SELECT ----------------------------------------------------

    Optional<Day> findDayById(long dayId);
}
