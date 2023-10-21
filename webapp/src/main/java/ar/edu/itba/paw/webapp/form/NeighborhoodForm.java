package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodConstraint;
import org.hibernate.validator.constraints.Range;

public class NeighborhoodForm {

    @NeighborhoodConstraint
    private long neighborhoodId;

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(long neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    @Override
    public String toString() {
        return "NeighborhoodForm{" +
                ", neighborhoodId='" + neighborhoodId + '\'' +
                '}';
    }
}
