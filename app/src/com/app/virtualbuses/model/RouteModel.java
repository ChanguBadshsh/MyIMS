package com.app.virtualbuses.model;

/**
 * Created by tasol on 8/5/17.
 */

public class RouteModel {
    String route_id;
    String bus_number;
    String start_station;
    String end_station;
    String line_number;
    String arrival_time;

    public String getRoute_id() {
        return route_id;
    }

    public String getBus_number() {
        return bus_number;
    }

    public String getStart_station() {
        return start_station;
    }

    public String getEnd_station() {
        return end_station;
    }

    public String getLine_number() {
        return line_number;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public RouteModel(String route_id, String bus_number, String start_station, String end_station, String line_number, String arrival_time) {
        this.route_id = route_id;
        this.bus_number = bus_number;
        this.start_station = start_station;
        this.end_station = end_station;
        this.line_number = line_number;
        this.arrival_time = arrival_time;


    }
}
