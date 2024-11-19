package ar.edu.itba.paw.models;

public class ThreeId {
    private final long firstId;
    private final long secondId;
    private final long thirdId;

    public ThreeId(long firstId, long secondId, long thirdId) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.thirdId = thirdId;
    }

    public long getFirstId() {
        return firstId;
    }

    public long getSecondId() {
        return secondId;
    }

    public long getThirdId() {
        return thirdId;
    }
}
