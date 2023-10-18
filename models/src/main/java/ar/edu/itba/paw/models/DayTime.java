package ar.edu.itba.paw.models;

import java.sql.Time;

public class DayTime {
    private Time openTime;
    private Time closeTime;

    // Default constructor
    public DayTime() {
    }

    public Time getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Time openTime) {
        this.openTime = openTime;
    }

    public Time getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Time closeTime) {
        this.closeTime = closeTime;
    }


    @Override
    public String toString() {
        return "DayTime {" +
                "openTime =" + openTime +
                ", closeTime ='" + closeTime + '\'' +
                '}';
    }
}
