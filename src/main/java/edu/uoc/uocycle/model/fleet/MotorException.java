package edu.uoc.uocycle.model.fleet;

public class MotorException extends BicycleException {

    public static final String INVALID_POWER = "Power must be greater than zero";
    public static final String INVALID_MAX_SPEED = "Max speed must be greater than zero";

    public MotorException(String message) {
        super(message);
    }
}
