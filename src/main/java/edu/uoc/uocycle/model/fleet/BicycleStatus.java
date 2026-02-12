package edu.uoc.uocycle.model.fleet;

public enum BicycleStatus {

    AVAILABLE("Available"),
    RESERVED("Reserved"),
    MAINTENANCE("Maintenance"),
    LOST("Lost");

    private final String description;

    BicycleStatus(String description) {
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
