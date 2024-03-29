package ar.edu.itba.paw.models;

public class TwoIds {
    private final long firstId;
    private final long secondId;

    public TwoIds(long firstId, long secondId) {
        this.firstId = firstId;
        this.secondId = secondId;
    }

    public long getFirstId() {
        return firstId;
    }

    public long getSecondId() {
        return secondId;
    }
}

