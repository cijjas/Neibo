package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Day;

import java.util.Optional;

public interface DayDao {

    // ------------------------------------------------ DAYS INSERT ----------------------------------------------------

    Day createDay(String day);

    // ------------------------------------------------ DAYS SELECT ----------------------------------------------------

    Optional<Day> findDay(long dayId);
}
