package com.app.virtualbuses.model;

/**
 * Created by tasol on 8/5/17.
 */

public class Stations {
    String station;
    String arrival_time;
    String departure_time;
    String bus_id;

    public Stations(String station, String arrival_time, String departure_time, String bus_id) {
        this.station = station;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.bus_id = bus_id;
    }

    public String getStation() {
        return station;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public String getBus_id() {
        return bus_id;
    }
}
