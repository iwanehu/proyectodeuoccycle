package edu.uoc.uocycle.model.station;

public class StationException extends Exception {

    public static final String INVALID_ID = "ID cannot be null or blank";
    public static final String INVALID_MAX_CAPACITY = "Max capacity must be greater than zero";
    public static final String BICYCLE_NULL = "Bicycle cannot be null";
    public static final String STATION_FULL = "Station is at full capacity";
    public static final String BICYCLE_ALREADY_EXISTS = "Bicycle already exists in the station";
    public static final String BICYCLE_NOT_FOUND = "Bicycle not found in the station";
    public static final String STATION_NOT_FOUND = "Station not found";

    public StationException(String message) {
        super(message);
    }

}
