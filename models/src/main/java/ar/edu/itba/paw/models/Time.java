package ar.edu.itba.paw.models;

public class Time {
    private long timeId;
    private String timeInterval;

    private Time(Builder builder) {
        this.timeId = builder.timeId;
        this.timeInterval = builder.timeInterval;
    }

    public static class Builder {
        private long timeId;
        private String timeInterval;

        public Builder timeId(long timeId) {
            this.timeId = timeId;
            return this;
        }

        public Builder timeInterval(String timeInterval) {
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

    public String getTimeInterval() {
        return timeInterval;
    }

    @Override
    public String toString() {
        return "Time{" +
                "timeId=" + timeId +
                ", timeInterval='" + timeInterval + '\'' +
                '}';
    }
}
