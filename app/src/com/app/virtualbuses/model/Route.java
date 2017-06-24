package com.app.virtualbuses.model;

/**
 * Created by tasol on 8/5/17.
 */

public class Route {
    String bus_id;
    String start_station;
    String end_station;
    String arrival_time;
    String departure_time;

    public Route(String bus_id, String start_station, String end_station, String arrival_time, String departure_time) {
        this.bus_id = bus_id;
        this.start_station = start_station;
        this.end_station = end_station;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
    }

    public String getBus_id() {
        return bus_id;
    }

    public String getStart_station() {
        return start_station;
    }

    public String getEnd_station() {
        return end_station;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }
}
