package ar.edu.itba.paw.models.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class WorkerAreaKey implements Serializable {
    private Long workerId;
    private Long neighborhoodId;

    public WorkerAreaKey() {
    }

    public WorkerAreaKey(Long workerId, Long neighborhoodId) {
        this.workerId = workerId;
        this.neighborhoodId = neighborhoodId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerAreaKey that = (WorkerAreaKey) o;
        return workerId.equals(that.workerId) && neighborhoodId.equals(that.neighborhoodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workerId, neighborhoodId);
    }
}
