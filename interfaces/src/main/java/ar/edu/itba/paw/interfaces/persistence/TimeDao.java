package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Time;
import enums.StandardTime;

import java.util.Optional;

public interface TimeDao {
    // Should not be used, as we can manually manipulate the DB
    Time createTime(String timeInterval);

    Optional<Time> findTimeById(long timeId);
}
