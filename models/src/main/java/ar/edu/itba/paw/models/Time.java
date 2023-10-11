package ar.edu.itba.paw.models;

public class Time {
    private long timeId;
    private java.sql.Time timeInterval;

    private Time(Builder builder) {
        this.timeId = builder.timeId;
        this.timeInterval = builder.timeInterval;
    }

    public static class Builder {
        private long timeId;
        private java.sql.Time timeInterval;

        public Builder timeId(long timeId) {
            this.timeId = timeId;
            return this;
        }

        public Builder timeInterval(java.sql.Time timeInterval) {
            this.timeInterval = timeInterval;
            return this;
        }

        public Time build() {
            return new Time(this);
        }
    }

    public long getTimeId() {
        return timeId;
    }

    public java.sql.Time getTimeInterval() {
        return timeInterval;
    }

    @Override
    public String toString() {
        return "Time{" +
                "timeId=" + timeId +
                ", timeInterval=" + timeInterval +
                '}';
    }
}
