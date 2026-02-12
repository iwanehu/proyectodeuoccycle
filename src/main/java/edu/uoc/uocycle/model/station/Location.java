package edu.uoc.uocycle.model.station;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Location {

    private double latitude;
    private double longitude;
    private String address;

    public Location(double latitude, double longitude, String address) throws LocationException {
        setLatitude(latitude);
        setLongitude(longitude);
        setAddress(address);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) throws LocationException {
        if (latitude < -90 || latitude > 90) {
            throw new LocationException(LocationException.INVALID_LATITUDE);
        }
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) throws LocationException {
        if (longitude < -180 || longitude > 180) {
            throw new LocationException(LocationException.INVALID_LONGITUDE);
        }
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) throws LocationException {
        if (address == null || address.isBlank()) {
            throw new LocationException(LocationException.INVALID_ADDRESS);
        }
        this.address = address;
    }

    public static double calculateDistance(Location loc1, Location loc2) {
        final int R = 6371;

        double latDistance = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double lonDistance = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(loc1.getLatitude())) * Math.cos(Math.toRadians(loc2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public String toString() {
        JsonObject loc = new JsonObject();
        loc.addProperty("latitude", getLatitude());
        loc.addProperty("longitude", getLongitude());
        loc.addProperty("address", getAddress());

        return new GsonBuilder().create().toJson(loc);
    }

}
