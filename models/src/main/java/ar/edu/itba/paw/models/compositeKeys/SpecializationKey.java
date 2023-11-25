package ar.edu.itba.paw.models.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class SpecializationKey implements Serializable {
    private Long workerId;
    private Long professionId;

    public SpecializationKey() {
    }

    public SpecializationKey(Long workerId, Long professionId) {
        this.workerId = workerId;
        this.professionId = professionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecializationKey)) return false;
        SpecializationKey that = (SpecializationKey) o;
        return Objects.equals(workerId, that.workerId) && Objects.equals(professionId, that.professionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workerId, professionId);
    }
}
