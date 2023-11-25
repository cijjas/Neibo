package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodsConstraint;

import javax.validation.constraints.NotNull;
import java.util.List;

public class NeighborhoodsForm {

    @NeighborhoodsConstraint
    @NotNull
    private String neighborhoodIds;

    public String getNeighborhoodIds() {
        return neighborhoodIds;
    }

    public void setNeighborhoodIds(String neighborhoodIds) {
        this.neighborhoodIds = neighborhoodIds;
    }

    @Override
    public String toString() {
        return "NeighborhoodForm{" +
                ", neighborhoodIds='" + neighborhoodIds + '\'' +
                '}';
    }
}
