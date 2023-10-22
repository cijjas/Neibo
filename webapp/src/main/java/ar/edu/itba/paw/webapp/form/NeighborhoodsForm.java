package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodsConstraint;

import java.util.List;

public class NeighborhoodsForm {

    @NeighborhoodsConstraint
    private List<Long> neighborhoodIds;

    public List<Long> getNeighborhoodIds() {
        return neighborhoodIds;
    }

    public void setNeighborhoodId(List<Long> neighborhoodIds) {
        this.neighborhoodIds = neighborhoodIds;
    }

    @Override
    public String toString() {
        return "NeighborhoodForm{" +
                ", neighborhoodIds='" + neighborhoodIds + '\'' +
                '}';
    }
}
