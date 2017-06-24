package com.app.virtualbuses.model;

/**
 * Created by tasol on 8/5/17.
 */

public class BusRouteModel {
    String source_station;
    String destination_station;
    String arrival_time;
    String departure_time;

    public BusRouteModel(String source_station, String destination_station, String arrival_time, String departure_time) {
        this.source_station = source_station;
        this.destination_station = destination_station;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
    }

    public String getSource_station() {
        return source_station;
    }

    public String getDestination_station() {
        return destination_station;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }
}
