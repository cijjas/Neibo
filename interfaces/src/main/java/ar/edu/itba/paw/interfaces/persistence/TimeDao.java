package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.MainEntities.Time;

import java.util.Optional;
import java.util.OptionalLong;

public interface TimeDao {

    // ----------------------------------------------- TIMES INSERT ----------------------------------------------------

    Time createTime(java.sql.Time timeInterval);

    // ----------------------------------------------- TIMES SELECT ----------------------------------------------------

    Optional<Time> findTimeById(long timeId);

    OptionalLong findIdByTime(java.sql.Time time);
}
