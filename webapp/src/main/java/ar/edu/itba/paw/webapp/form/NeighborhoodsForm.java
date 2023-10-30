package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodsConstraint;

import java.util.List;

public class NeighborhoodsForm {

    @NeighborhoodsConstraint
    private String neighborhoodIds;

    public String getNeighborhoodIds() {
        return neighborhoodIds;
    }

    public void setNeighborhoodId(String neighborhoodIds) {
        this.neighborhoodIds = neighborhoodIds;
    }

    @Override
    public String toString() {
        return "NeighborhoodForm{" +
                ", neighborhoodIds='" + neighborhoodIds + '\'' +
                '}';
    }
}
