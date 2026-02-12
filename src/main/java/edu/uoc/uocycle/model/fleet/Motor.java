package edu.uoc.uocycle.model.fleet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Motor {

    private int power;
    private double maxSpeed;
    private boolean hasRegenerativeBraking;

    public Motor(int power, double maxSpeed, boolean hasRegenerativeBraking) throws MotorException {
        setPower(power);
        setMaxSpeed(maxSpeed);
        setHasRegenerativeBraking(hasRegenerativeBraking);
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) throws MotorException {
        if (power <= 0) {
            throw new MotorException(MotorException.INVALID_POWER);
        }
        this.power = power;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) throws MotorException {
        if (maxSpeed <= 0) {
            throw new MotorException(MotorException.INVALID_MAX_SPEED);
        }
        this.maxSpeed = maxSpeed;
    }

    public boolean hasRegenerativeBraking() {
        return hasRegenerativeBraking;
    }

    public void setHasRegenerativeBraking(boolean hasRegenerativeBraking) {
        this.hasRegenerativeBraking = hasRegenerativeBraking;
    }

    @Override
    public String toString() {
        JsonObject root = new JsonObject();
        root.addProperty("power", getPower());
        root.addProperty("maxSpeed", getMaxSpeed());
        root.addProperty("hasRegenerativeBraking", hasRegenerativeBraking());

        return new GsonBuilder().setPrettyPrinting().create().toJson(root);
    }

}
