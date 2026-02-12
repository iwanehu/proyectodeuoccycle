package edu.uoc.uocycle.controller;

import edu.uoc.uocycle.model.fleet.Bicycle;
import edu.uoc.uocycle.model.fleet.BicycleFactory;
import edu.uoc.uocycle.model.fleet.BicycleException;
import edu.uoc.uocycle.model.station.Station;
import edu.uoc.uocycle.model.station.StationException;
import edu.uoc.uocycle.model.trip.Trip;
import edu.uoc.uocycle.model.trip.TripException;
import edu.uoc.uocycle.model.user.User;
import edu.uoc.uocycle.model.user.UserException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UOCycleController {

    /**
     * List of Stations
     */
    private List<Station> stations;

    /**
     * Has table of Bicycles
     */
    private Map<String, Bicycle> bicycles;

    /**
     * Current User
     */
    private User user;

    /**
     * Constructor
     * Initializes the attributes and loads the data from the files
     * @param stationsFilePath Path to the stations data file
     * @param bicyclesFilePath Path to the bicycles data file
     * @param bicyclesStationOriginFilePath Path to the bicycles-station origin data file
     * @throws Exception If an error occurs while loading the data
     */
    public UOCycleController(String stationsFilePath, String bicyclesFilePath, String bicyclesStationOriginFilePath) throws Exception {
        this.stations = new LinkedList<>();
        this.bicycles = new HashMap<>();
        this.user = null;

        loadStations(stationsFilePath);
        loadBicycles(bicyclesFilePath, bicyclesStationOriginFilePath);
    }

    /**
     * Reads a table from a file
     * @param filename Name of the file to read
     * @param expectedParts Expected number of parts per line (0 for any)
     * @return List of string arrays, each array representing a line split by '|'
     */
    private List<String[]> readTable(String filename, int expectedParts) {
        List<String[]> rows = new ArrayList<>();
        String path = "/data/" + filename;

        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("File not found: " + path);
                return rows;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                int lineNo = 0;
                while ((line = br.readLine()) != null) {
                    lineNo++;
                    String raw = line;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    String[] parts = line.split("\\|", -1);
                    if (expectedParts > 0 && parts.length != expectedParts) {
                        System.err.printf("Invalid line format (%s:%d): %s%n", filename, lineNo, raw);
                        continue;
                    }
                    for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
                    rows.add(parts);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading file " + path + ": " + e.getMessage());
        }
        return rows;
    }

    /**
     * Loads stations from file
     * @param filename Name of the file containing station data
     * @throws Exception If an error occurs while loading stations
     */
    private void loadStations(String filename) throws Exception {
        // id|lat|lon|address|maxCapacity
        for (String[] parts : readTable(filename, 5)) {
            String id = parts[0];
            double latitude = Double.parseDouble(parts[1]);
            double longitude = Double.parseDouble(parts[2]);
            String address = parts[3];
            int maxCapacity = Integer.parseInt(parts[4]);
            addStation(id, latitude, longitude, address, maxCapacity);
        }
    }

    /**
     * Loads bicycles from files
     * @param bicyclesFilePath Path to the bicycles data file
     * @param bicyclesStationOriginFilePath Path to the bicycles-station origin data file
     * @throws Exception If an error occurs while loading bicycles
     */
    private void loadBicycles(String bicyclesFilePath, String bicyclesStationOriginFilePath) throws Exception {
        // type|args
        for (String[] parts : readTable(bicyclesFilePath, 2)) {
            addBicycle(parts[0], parts[1]);
        }
        // bicycleId|stationId
        for (String[] parts : readTable(bicyclesStationOriginFilePath, 2)) {
            addBicycleToStation(parts[0], parts[1]);
        }
    }

    /**
     * Adds a station to the list
     * @param id Station ID
     * @param latitude Station location latitude
     * @param longitude Station location longitude
     * @param address Station location address
     * @param maxCapacity Station maximum capacity
     * @throws Exception If an error occurs while creating the station or adding it to the list
     */
    public void addStation(String id, double latitude, double longitude, String address, int maxCapacity) throws Exception {
        Station station = new Station(id, latitude, longitude, address, maxCapacity);
        stations.add(station);
    }

    /**
     * Adds a bicycle to the hashtable
     * @param type Type of bicycle (MECHANICAL or ELECTRICAL)
     * @param args Arguments for the bicycle constructor
     * @throws Exception If an error occurs while creating the bicycle or adding it to the hashtable
     */
    public void addBicycle(String type, String args) throws Exception {
        Bicycle bicycle = BicycleFactory.create(type, args);
        bicycles.put(bicycle.getId(), bicycle);
    }

    /**
     * Adds a bicycle to a station
     * @param bicycleId ID of the bicycle to add
     * @param stationId ID of the station to which the bicycle will be added
     * @throws Exception If the bicycle or station is not found, or if an error occurs while adding the bicycle to the station
     */
    public void addBicycleToStation(String bicycleId, String stationId) throws Exception {
        Bicycle bicycle = bicycles.get(bicycleId);

        if (bicycle == null) {
            throw new BicycleException(BicycleException.BICYCLE_NOT_FOUND);
        }

        Station station = findStationById(stationId);

        if (station == null) {
            throw new StationException(StationException.STATION_NOT_FOUND);
        }

        station.addBicycle(bicycle);
    }

    /**
     * Returns the list of stations as an array
     * @return Array of stations
     */
    public Object[] getStations() {
        Station[] stationArray = new Station[stations.size()];

        for (int i = 0; i < stations.size(); i++) {
            stationArray[i] = stations.get(i);
        }

        return stationArray;
    }

    /**
     * Returns the list of bicycles as an array
     * @return Array of bicycles
     */
    public Object[] getBicycles() {
        Bicycle[] bicycleArray = new Bicycle[bicycles.size()];

        int i = 0;
        for (Bicycle bicycle : bicycles.values()) {
            bicycleArray[i++] = bicycle;
        }

        return bicycleArray;
    }

    /**
     * Returns the bicycles of a station as an array
     * @return Array of bicycles in the specified station
     * @throws Exception If the station is not found
     */
    public Object[] getBicyclesByStation(String stationId) throws Exception {
        Station station = findStationById(stationId);

        if (station == null) {
            throw new StationException(StationException.STATION_NOT_FOUND);
        }

        Bicycle[] bicycleArray = new Bicycle[station.getBicycles().size()];

        for (int i = 0; i < station.getBicycles().size(); i++) {
            bicycleArray[i] = station.getBicycles().get(i);
        }

        return bicycleArray;
    }

    /**
     * Creates a new user
     * @param userName Name of the user
     * @throws Exception If an error occurs while creating the user
     */
    public void createUser(String userName) throws Exception {
        this.user = new User(userName);
    }

    /**
     * Returns the current username
     * @return Current username
     */
    public String getUserName() {
        return user != null ? user.getName() : null;
    }

    /**
     * Starts a trip by selecting the start station and bicycle
     * @param stationId The ID of the start station
     * @param bicycleId The ID of the selected bicycle
     * @throws Exception If the station or user is not found, or if an error occurs while starting the trip
     */
    public void startTrip(String stationId, String bicycleId) throws Exception {
        Station startStation = findStationById(stationId);

        if (startStation == null) {
            throw new StationException(StationException.STATION_NOT_FOUND);
        }

        if (user == null) {
            throw new UserException(UserException.USER_NOT_FOUND);
        }

        if (isTripStarted()) {
            throw new TripException(TripException.USER_ALREADY_ON_TRIP);
        }

        Bicycle selectedBicycle = bicycles.get(bicycleId);

        startStation.removeBicycle(selectedBicycle);
        user.startTrip(selectedBicycle, startStation);
    }

    /**
     * Ends the current trip by selecting the end station
     * @param stationId The ID of the end station
     * @return A JSON string with the trip summary
     * @throws Exception If the station or user is not found, or if an error occurs while ending the trip
     */
    public String endTrip(String stationId) throws Exception {
        Station endStation = findStationById(stationId);

        if (user == null) {
            throw new UserException(UserException.USER_NOT_FOUND);
        }

        if (endStation == null) {
            throw new StationException(StationException.STATION_NOT_FOUND);
        }

        if (!isTripStarted()) {
            throw new TripException(TripException.USER_NOT_ON_TRIP);
        }

        Bicycle bicycle = user.getCurrentBicycle();
        endStation.addBicycle(bicycle);
        return user.endTrip(endStation).toString();
    }

    /**
     * Checks if a trip has been started
     * @return True if a trip has been started, otherwise false
     */
    public boolean isTripStarted() {
        return user != null && user.isOnTrip();
    }

    /**
     * Returns the current trip of the user as a JSON string
     * @return Current trip in JSON format
     */
    public String getCurrentTrip() {
        if (user == null || !user.isOnTrip()) return null;

        return user.getCurrentTrip().toString();
    }

    /**
     * Returns the trips of the current user as an array of strings
     * @return Array of trips in JSON format
     */
    public String[] getTrips() {
        if (user == null) return new String[0];

        return user.getTrips()
                .stream()
                .map(Trip::toString)
                .toArray(String[]::new);
    }

    // Auxiliary methods

    /**
     * Finds a station by its ID
     * @param stationId ID of the station to find
     * @return The station with the specified ID, or null if not found
     */
    private Station findStationById(String stationId) {
        if (stationId == null) return null;
        return stations.stream()
                .filter(s -> stationId.equals(s.getId()))
                .findFirst()
                .orElse(null);
    }
}
