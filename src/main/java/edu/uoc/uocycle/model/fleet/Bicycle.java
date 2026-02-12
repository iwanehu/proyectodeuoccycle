package edu.uoc.uocycle.model.fleet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.time.LocalDate;

public abstract class Bicycle {

    private String id;
    private BicycleStatus status;
    private double weight;
    private LocalDate registrationDate;
    private LocalDate lastMaintenanceDate;

    public Bicycle(String id, BicycleStatus status, double weight, LocalDate registrationDate, LocalDate lastMaintenanceDate) throws BicycleException {
        setId(id);
        setStatus(status);
        setWeight(weight);
        setRegistrationDate(registrationDate);
        setLastMaintenanceDate(lastMaintenanceDate);
    }

    public String getId() {
        return id;
    }

    private void setId(String id) throws BicycleException {
        if (id == null || id.isBlank()) {
            throw new BicycleException(BicycleException.INVALID_BIKE_ID);
        }
        this.id = id;
    }

    public BicycleStatus getStatus() {
        return status;
    }

    public void setStatus(BicycleStatus status) throws BicycleException {
        if (status == null) {
            throw new BicycleException(BicycleException.INVALID_BIKE_STATUS);
        }
        this.status = status;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) throws BicycleException {
        if (weight <= 0) {
            throw new BicycleException(BicycleException.INVALID_BIKE_WEIGHT);
        }
        this.weight = weight;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) throws BicycleException {
        if (registrationDate == null || registrationDate.isAfter(LocalDate.now())) {
            throw new BicycleException(BicycleException.INVALID_BIKE_REGISTRATION_DATE);
        }
        this.registrationDate = registrationDate;
    }

    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) throws BicycleException {
        if (lastMaintenanceDate == null || lastMaintenanceDate.isBefore(registrationDate) || lastMaintenanceDate.isAfter(LocalDate.now())) {
            throw new BicycleException(BicycleException.INVALID_BIKE_LAST_MAINTENANCE_DATE);
        }
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    @Override
    public String toString() {
        JsonObject root = new JsonObject();
        root.addProperty("id", getId());
        root.addProperty("status", getStatus() != null ? getStatus().toString() : null);
        root.addProperty("weight", getWeight());
        root.addProperty("registrationDate", getRegistrationDate() != null ? getRegistrationDate().toString() : null);
        root.addProperty("lastMaintenanceDate", getLastMaintenanceDate() != null ? getLastMaintenanceDate().toString() : null);

        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(root);
    }

}
