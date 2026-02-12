package edu.uoc.uocycle.model.fleet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Battery {

    private int capacityWh;
    private int voltageV;
    private double currentAh;

    public Battery(int capacityWh, int voltageV, double currentAh) throws BatteryException {
        setCapacityWh(capacityWh);
        setVoltageV(voltageV);
        setCurrentAh(currentAh);
    }

    public int getCapacityWh() {
        return capacityWh;
    }

    public void setCapacityWh(int capacityWh) throws BatteryException {
        if (capacityWh <= 0) {
            throw new BatteryException(BatteryException.INVALID_CAPACITY);
        }
        this.capacityWh = capacityWh;
    }

    public int getVoltageV() {
        return voltageV;
    }

    public void setVoltageV(int voltageV) throws BatteryException {
        if (voltageV <= 0) {
            throw new BatteryException(BatteryException.INVALID_VOLTAGE);
        }
        this.voltageV = voltageV;
    }

    public double getCurrentAh() {
        return currentAh;
    }

    public void setCurrentAh(double currentAh) throws BatteryException {
        if (currentAh <= 0) {
            throw new BatteryException(BatteryException.INVALID_CURRENT);
        }
        this.currentAh = currentAh;
    }

    @Override
    public String toString() {
        JsonObject root = new JsonObject();
        root.addProperty("capacityWh", getCapacityWh());
        root.addProperty("voltageV", getVoltageV());
        root.addProperty("currentAh", getCurrentAh());

        return new GsonBuilder().setPrettyPrinting().create().toJson(root);
    }

}
