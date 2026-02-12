package edu.uoc.uocycle.model.station;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.uoc.uocycle.model.fleet.Bicycle;

import java.util.LinkedList;
import java.util.List;

public class Station {
    
    private String id;
    private Location location;
    private final int MAX_CAPACITY;
    private List<Bicycle> bicycles;

    public Station(String id, double latitude, double longitude, String address, int maxCapacity) throws StationException {
        setId(id);
        setLocation(latitude, longitude, address);

        if (maxCapacity <= 0) {
            throw new StationException(StationException.INVALID_MAX_CAPACITY);
        }
        this.MAX_CAPACITY = maxCapacity;

        this.bicycles = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    private void setId(String id) throws StationException {
        if (id == null || id.isBlank()) {
            throw new StationException(StationException.INVALID_ID);
        }
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(double latitude, double longitude, String address) throws LocationException {
        this.location = new Location(latitude, longitude, address);
    }

    public int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public List<Bicycle> getBicycles() {
        return new LinkedList<>(bicycles);
    }

    public boolean isFull() {
        return bicycles.size() >= getMaxCapacity();
    }

    public void addBicycle(Bicycle bicycle) throws StationException {
        if (bicycle == null) {
            throw new StationException(StationException.BICYCLE_NULL);
        }
        if (bicycles.contains(bicycle)) {
            throw new StationException(StationException.BICYCLE_ALREADY_EXISTS);
        }
        if (isFull()) {
            throw new StationException(StationException.STATION_FULL);
        }
        bicycles.add(bicycle);
    }

    public void removeBicycle(Bicycle bicycle) throws StationException {
        if (bicycle == null) {
            throw new StationException(StationException.BICYCLE_NULL);
        }
        if (!bicycles.remove(bicycle)) {
            throw new StationException(StationException.BICYCLE_NOT_FOUND);
        }
    }

    public static double calculateDistance(Station s1, Station s2) {
        return Location.calculateDistance(s1.getLocation(), s2.getLocation());
    }

    @Override
    public String toString() {
        JsonObject root = new JsonObject();
        root.addProperty("id", getId());

        JsonObject loc = JsonParser.parseString(getLocation().toString()).getAsJsonObject();
        root.add("location", loc);

        root.addProperty("MAX_CAPACITY", getMaxCapacity());

        return new GsonBuilder().setPrettyPrinting().create().toJson(root);
    }

}
