package edu.uoc.uocycle.model.trip;

public class TripException extends Exception {

    public static final String INVALID_BICYCLE = "Bicycle cannot be null";
    public static final String INVALID_START_STATION = "Start station cannot be null";
    public static final String INVALID_END_STATION = "End station cannot be null";
    public static final String INVALID_START_TIME = "Start time cannot be null";
    public static final String INVALID_END_TIME = "End time cannot be null or before start time";
    public static final String BICYCLE_NOT_AVAILABLE = "Bicycle is not available for the trip";
    public static final String USER_ALREADY_ON_TRIP = "User is already on a trip";
    public static final String USER_NOT_ON_TRIP = "User is not currently on a trip";

    public TripException(String message) {
        super(message);
    }
}
