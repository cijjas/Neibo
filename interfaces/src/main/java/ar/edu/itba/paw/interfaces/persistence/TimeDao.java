package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Time;

import java.util.Optional;
import java.util.OptionalLong;

public interface TimeDao {

    // ----------------------------------------------- TIMES INSERT ----------------------------------------------------

    Time createTime(java.sql.Time timeInterval);

    // ----------------------------------------------- TIMES SELECT ----------------------------------------------------

    Optional<Time> findTime(long timeId);

    OptionalLong findId(java.sql.Time time);
}
