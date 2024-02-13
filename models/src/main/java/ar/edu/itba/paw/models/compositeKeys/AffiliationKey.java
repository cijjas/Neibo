package ar.edu.itba.paw.models.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class AffiliationKey implements Serializable {
    private Long workerId;
    private Long neighborhoodId;

    public AffiliationKey() {
    }

    public AffiliationKey(Long workerId, Long neighborhoodId) {
        this.workerId = workerId;
        this.neighborhoodId = neighborhoodId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AffiliationKey)) return false;
        AffiliationKey that = (AffiliationKey) o;
        return Objects.equals(workerId, that.workerId) && Objects.equals(neighborhoodId, that.neighborhoodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workerId, neighborhoodId);
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(Long neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }
}
