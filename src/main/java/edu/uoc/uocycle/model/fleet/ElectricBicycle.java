package edu.uoc.uocycle.model.fleet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDate;

public final class ElectricBicycle extends Bicycle {

    private Battery battery;
    private Motor motor;

    public ElectricBicycle(String id, BicycleStatus status, double weight, LocalDate registrationDate, LocalDate lastMaintenanceDate,
                           int batteryCapacity, int batteryVoltage, double batteryCurrent,
                           int motorPower, double motorMaxSpeed, boolean motorHasRegenerativeBraking) throws BicycleException {
        super(id, status, weight, registrationDate, lastMaintenanceDate);
        setBattery(batteryCapacity, batteryVoltage, batteryCurrent);
        setMotor(motorPower, motorMaxSpeed, motorHasRegenerativeBraking);
    }

    public Battery getBattery() {
        return battery;
    }

    public void setBattery(int capacityWh, int voltageV, double currentAh) throws BatteryException {
        this.battery = new Battery(capacityWh, voltageV, currentAh);
    }

    public Motor getMotor() {
        return motor;
    }

    public void setMotor(int power, double maxSpeed, boolean hasRegenerativeBraking) throws MotorException {
        this.motor = new Motor(power, maxSpeed, hasRegenerativeBraking);
    }

    @Override
    public String toString() {
        JsonObject root = JsonParser.parseString(super.toString()).getAsJsonObject();

        root.addProperty("type", "ELECTRIC");

        // Battery
        if (getBattery() != null) {
            JsonObject bat = JsonParser.parseString(getBattery().toString()).getAsJsonObject();
            root.add("battery", bat);
        }

        // Motor
        if (getMotor() != null) {
            JsonObject mot = JsonParser.parseString(getMotor().toString()).getAsJsonObject();
            root.add("motor", mot);
        }

        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(root);
    }

}
