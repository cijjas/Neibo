package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

public class NeighborhoodForm {

    @NotBlank
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
