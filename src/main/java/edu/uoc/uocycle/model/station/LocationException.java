package edu.uoc.uocycle.model.station;

public class LocationException extends StationException {

    public static final String INVALID_LATITUDE = "Latitude must be between -90 and 90 degrees.";
    public static final String INVALID_LONGITUDE = "Longitude must be between -180 and 180 degrees.";
    public static final String INVALID_ADDRESS = "Address cannot be null or empty.";

    public LocationException(String message) {
        super(message);
    }

}
