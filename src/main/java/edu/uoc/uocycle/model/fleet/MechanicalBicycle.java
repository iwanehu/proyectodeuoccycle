package edu.uoc.uocycle.model.fleet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDate;

public final class MechanicalBicycle extends Bicycle {

    private int gearCount;
    private GearType gearType;
    private boolean hasBasket;

    public MechanicalBicycle(String id, BicycleStatus status, double weight, LocalDate registrationDate, LocalDate lastMaintenanceDate,
                             int gearCount, GearType gearType, boolean hasBasket) throws BicycleException {
        super(id, status, weight, registrationDate, lastMaintenanceDate);
        setGearCount(gearCount);
        setGearType(gearType);
        setHasBasket(hasBasket);
    }

    public int getGearCount() {
        return gearCount;
    }

    public void setGearCount(int gearCount) throws BicycleException {
        if (gearCount <= 0) {
            throw new BicycleException(BicycleException.INVALID_GEAR_COUNT);
        }
        this.gearCount = gearCount;
    }

    public GearType getGearType() {
        return gearType;
    }

    public void setGearType(GearType gearType) throws BicycleException {
        if (gearType == null) {
            throw new BicycleException(BicycleException.INVALID_GEAR_TYPE);
        }
        this.gearType = gearType;
    }

    public boolean hasBasket() {
        return hasBasket;
    }

    public void setHasBasket(boolean hasBasket) {
        this.hasBasket = hasBasket;
    }

    @Override
    public String toString() {
        JsonObject root = JsonParser.parseString(super.toString()).getAsJsonObject();

        root.addProperty("type", "MECHANICAL");

        JsonObject mech = new JsonObject();
        mech.addProperty("gearCount", getGearCount());
        mech.addProperty("gearType",  getGearType() != null ? getGearType().toString() : null);
        mech.addProperty("hasBasket", hasBasket());

        root.add("mechanical", mech);

        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(root);
    }

}
