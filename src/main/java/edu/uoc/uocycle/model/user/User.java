package edu.uoc.uocycle.model.user;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.uoc.uocycle.model.fleet.Bicycle;
import edu.uoc.uocycle.model.station.Station;
import edu.uoc.uocycle.model.trip.Trip;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class User {

    private String name;
    private Trip currentTrip;
    private List<Trip> tripList;

    public User(String name) throws UserException {
        setName(name);
        setCurrentTrip(null);
        this.tripList = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws UserException {
        if (name == null || name.isBlank()) {
            throw new UserException(UserException.INVALID_NAME);
        }
        this.name = name;
    }

    public Trip getCurrentTrip() {
        return currentTrip;
    }

    public void setCurrentTrip(Trip currentTrip) {
        this.currentTrip = currentTrip;
    }

    public List<Trip> getTrips() {
        return tripList;
    }

    public void startTrip(Bicycle bicycle, Station startStation) throws Exception {
        Trip trip = new Trip(bicycle, startStation, LocalDateTime.now());
        setCurrentTrip(trip);
        this.tripList.add(trip);
    }

    public Trip endTrip(Station endStation) {
        Trip trip = getCurrentTrip();

        if (trip != null) {
            try {
                trip.setEndStation(endStation);
                trip.setEndTime(LocalDateTime.now());
                trip.computeDistance();
                setCurrentTrip(null);
            } catch (Exception e) {
                System.out.println("Error ending trip: " + e.getMessage());
            }
        }

        return trip;
    }

    public boolean isOnTrip() {
        return this.currentTrip != null;
    }

    public Bicycle getCurrentBicycle() {
        if (isOnTrip()) {
            return getCurrentTrip().getBicycle();
        }
        return null;
    }

    @Override
    public String toString() {
        JsonObject root = new JsonObject();

        root.addProperty("name", getName());
        root.addProperty("onTrip", isOnTrip());

        JsonArray trips = new JsonArray();
        for (Trip t : getTrips()) {
            trips.add(JsonParser.parseString(t.toString()));
        }
        root.add("trips", trips);

        return new GsonBuilder().setPrettyPrinting().create().toJson(root);
    }

}
