package edu.uoc.uocycle.model.fleet;

public class BatteryException extends BicycleException {

    public static final String INVALID_CAPACITY = "Capacity must be greater than zero";
    public static final String INVALID_VOLTAGE = "Voltage must be greater than zero";
    public static final String INVALID_CURRENT = "Current must be greater than zero";

    public BatteryException(String message) {
        super(message);
    }

}
