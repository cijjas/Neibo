package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Time;

import java.util.Optional;
import java.util.OptionalLong;

public interface TimeDao {

    // ----------------------------------------------- TIMES INSERT ----------------------------------------------------

    // Should not be used, as we can manually manipulate the DB
    Time createTime(java.sql.Time timeInterval);

    // ----------------------------------------------- TIMES SELECT ----------------------------------------------------

    Optional<Time> findTimeById(long timeId);

    OptionalLong findIdByTime(java.sql.Time time);
}
