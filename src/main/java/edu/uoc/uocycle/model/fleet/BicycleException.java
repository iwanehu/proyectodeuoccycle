package edu.uoc.uocycle.model.fleet;

public class BicycleException extends Exception {

    public static final String INVALID_BIKE_ID = "Bike ID cannot be null or blank";
    public static final String INVALID_BIKE_STATUS = "Bike status cannot be null";
    public static final String INVALID_BIKE_WEIGHT = "Bike weight must be greater than zero";
    public static final String INVALID_BIKE_REGISTRATION_DATE = "Bike registration date cannot be null or in the future";
    public static final String INVALID_BIKE_LAST_MAINTENANCE_DATE = "Last maintenance date cannot be null, before registration date or in the future";
    public static final String INVALID_GEAR_COUNT = "Gear count must be greater than zero";
    public static final String INVALID_GEAR_TYPE = "Gear type cannot be null";
    public static final String INVALID_TYPE = "Type cannot be null and must be either MECHANICAL or ELECTRICAL";
    public static final String INVALID_ARGUMENTS = "Invalid arguments for the selected bike type";
    public static final String BICYCLE_NOT_FOUND = "Bicycle not found";

    public BicycleException(String message) {
        super(message);
    }

}
