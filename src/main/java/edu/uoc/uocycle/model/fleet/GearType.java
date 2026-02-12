package edu.uoc.uocycle.model.fleet;

public enum GearType {
    DERAILLEUR("Derailleur"),
    HUB("Hub"),
    SINGLE_SPEED("Single Speed"),
    FIXED_GEAR("Fixed Gear");

    private final String description;

    GearType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
