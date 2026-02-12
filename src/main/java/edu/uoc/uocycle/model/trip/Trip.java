package edu.uoc.uocycle.model.trip;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.uoc.uocycle.model.fleet.Bicycle;
import edu.uoc.uocycle.model.fleet.BicycleStatus;
import edu.uoc.uocycle.model.station.Station;

import java.time.LocalDateTime;

public class Trip {

    private Bicycle bicycle;
    private Station startStation;
    private LocalDateTime startTime;
    private Station endStation;
    private LocalDateTime endTime;
    private double distance;

    public Trip(Bicycle bicycle, Station startStation, LocalDateTime startTime) throws TripException {
        setBicycle(bicycle);
        setStartStation(startStation);
        setStartTime(startTime);
    }

    public Bicycle getBicycle() {
        return bicycle;
    }

    public void setBicycle(Bicycle bicycle) throws TripException {
        if (bicycle == null) {
            throw new TripException(TripException.INVALID_BICYCLE);
        }
        if (bicycle.getStatus() != BicycleStatus.AVAILABLE) {
            throw new TripException(TripException.BICYCLE_NOT_AVAILABLE);
        }
        this.bicycle = bicycle;
    }

    public Station getStartStation() {
        return startStation;
    }

    public void setStartStation(Station startStation) throws TripException {
        if (startStation == null) {
            throw new TripException(TripException.INVALID_START_STATION);
        }
        this.startStation = startStation;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) throws TripException {
        if (startTime == null) {
            throw new TripException(TripException.INVALID_START_TIME);
        }
        this.startTime = startTime;
    }

    public Station getEndStation() {
        return endStation;
    }

    public void setEndStation(Station endStation) throws TripException {
        if (endStation == null) {
            throw new TripException(TripException.INVALID_END_STATION);
        }
        this.endStation = endStation;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) throws TripException {
        if (endTime == null) {
            throw new TripException(TripException.INVALID_END_TIME);
        }
        this.endTime = endTime;
    }

    public double getDistance() {
        return distance;
    }

    public void computeDistance() {
        this.distance = Station.calculateDistance(startStation, endStation);
    }

    @Override
    public String toString() {
        JsonObject root = new JsonObject();

        root.add("bicycle", JsonParser.parseString(getBicycle().toString()).getAsJsonObject());
        root.add("startStation", JsonParser.parseString(getStartStation().toString()).getAsJsonObject());
        root.addProperty("startTime", getStartTime().toString());

        if (getEndStation() != null) {
            root.add("endStation", JsonParser.parseString(getEndStation().toString()).getAsJsonObject());
        } else {
            root.add("endStation", null);
        }

        if (getEndTime() != null) {
            root.addProperty("endTime", getEndTime().toString());
        } else {
            root.add("endTime", null);
        }

        root.addProperty("distance", Math.round(getDistance() * 100.0) / 100.0);

        return new GsonBuilder().setPrettyPrinting().create().toJson(root);
    }


}
