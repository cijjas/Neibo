package ar.edu.itba.paw.webapp.form;

public class NeighborhoodsForm {

    private long[] neighborhoodIds;

    public long[] getNeighborhoodIds() {
        return neighborhoodIds;
    }

    public void setNeighborhoodId(long[] neighborhoodIds) {
        this.neighborhoodIds = neighborhoodIds;
    }

    @Override
    public String toString() {
        return "NeighborhoodForm{" +
                ", neighborhoodIds='" + neighborhoodIds + '\'' +
                '}';
    }
}
