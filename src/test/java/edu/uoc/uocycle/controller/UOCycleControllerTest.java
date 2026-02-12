package edu.uoc.uocycle.controller;

import com.google.gson.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static edu.uoc.uocycle.controller.JSONData.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UOCycleControllerTest {

    @Test
    @Order(1)
    @Tag("basic")
    @DisplayName("Basic - Add Station")
    public void testAddStation() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");
            assertEquals(23, controller.getStations().length, "Should load 23 stations");

            for (int i = 0; i < controller.getStations().length; i++) {
                String prettyJson = controller.getStations()[i].toString();
                JsonElement parsed = JsonParser.parseString(prettyJson);
                Gson gson = new GsonBuilder().create();
                String stationJson = gson.toJson(parsed);

                assertEquals(STATIONS_EXPECTED[i], stationJson, "Station " + (i + 1) + " should match expected");
            }
        } catch (Exception e) {
            System.out.println("Error in testAddStation: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @Tag("basic")
    @DisplayName("Basic - Invalid Station Id")
    public void testAddStationInvalidId() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            assertThrows(Exception.class, () -> controller.addStation(null, 75.0, 153.0, "Fake Street", 10));
            assertThrows(Exception.class, () -> controller.addStation("", 75.0, 153.0, "Fake Street", 10));
            assertThrows(Exception.class, () -> controller.addStation("   ", 75.0, 153.0, "Fake Street", 10));
        } catch (Exception e) {
            System.out.println("Error in testAddStationInvalidId: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @Tag("basic")
    @DisplayName("Basic - Invalid Station Location")
    public void testAddStationInvalidLocation() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            assertThrows(Exception.class, () -> controller.addStation("FakeID", 90.1, 153.0, "Fake Street", 10));
            assertThrows(Exception.class, () -> controller.addStation("FakeID", -90.1, 153.0, "Fake Street", 10));
            assertThrows(Exception.class, () -> controller.addStation("FakeID", 75.4, 180.1, "Fake Street", 10));
            assertThrows(Exception.class, () -> controller.addStation("FakeID", 75.4, -180.1, "Fake Street", 10));
        } catch (Exception e) {
            System.out.println("Error in testAddStationInvalidId: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @Tag("basic")
    @DisplayName("Basic - Invalid Station Address")
    public void testAddStationInvalidAddress() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            assertThrows(Exception.class, () -> controller.addStation("FakeID", 75.0, 153.0, null, 10));
            assertThrows(Exception.class, () -> controller.addStation("FakeID", 75.0, 153.0, "", 10));
            assertThrows(Exception.class, () -> controller.addStation("FakeID", 75.0, 153.0, "   ", 10));
        } catch (Exception e) {
            System.out.println("Error in testAddStationInvalidAddress: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @Tag("basic")
    @DisplayName("Basic - Invalid Station Capacity")
    public void testAddStationInvalidCapacity() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            assertThrows(Exception.class, () -> controller.addStation("FakeID", 75.0, 153.0, "Fake Street", -5));
            assertThrows(Exception.class, () -> controller.addStation("FakeID", 75.0, 153.0, "Fake Street", 0));
        } catch (Exception e) {
            System.out.println("Error in testAddStationInvalidCapacity: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    @Tag("basic")
    @DisplayName("Basic - Add Bicycle")
    public void testAddBicycle() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");
            assertEquals(314, controller.getBicycles().length, "Should load 314 bicycles");

            for (int i = 0; i < controller.getBicycles().length; i++) {
                String prettyJson = controller.getBicycles()[i].toString();
                JsonElement parsed = JsonParser.parseString(prettyJson);
                Gson gson = new GsonBuilder().create();
                String bicycleJson = gson.toJson(parsed);

                assertEquals(BICYCLES_EXPECTED[i], bicycleJson, "Bicycle " + (i + 1) + " should match expected");
            }
        } catch (Exception e) {
            System.out.println("Error in testAddBicycle: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    @Tag("basic")
    @DisplayName("Basic - Invalid Bicycle Type")
    public void testAddBicycleInvalidType() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            assertThrows(Exception.class, () -> controller.addBicycle(null, "Bike-001,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,9.7,500,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("", "Bike-001,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,9.7,500,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("   ", "Bike-001,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,9.7,500,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("FLYING", "Bike-001,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,9.7,500,34.1,false"));
        } catch (Exception e) {
            System.out.println("Error in testAddBicycleInvalidType: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    @Tag("basic")
    @DisplayName("Basic - Invalid Bicycle Arguments")
    public void testAddBicycleInvalidArguments() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", null));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", ""));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "   "));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,AVAILABLE,28.4"));

            // Invalid id
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", ",AVAILABLE,28.4,2020-01-13,2020-12-01,21,FIXED_GEAR,true"));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "   ,AVAILABLE,28.4,2020-01-13,2020-12-01,21,FIXED_GEAR,true"));

            // Invalid status
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,,28.4,2020-01-13,2020-12-01,21,FIXED_GEAR,true"));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,INVALID_STATUS,28.4,2020-01-13,2020-12-01,21,FIXED_GEAR,true"));

            // Invalid height
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,AVAILABLE,0.0,2020-01-13,2020-12-01,21,FIXED_GEAR,true"));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,AVAILABLE,-0.1,2020-01-13,2020-12-01,21,FIXED_GEAR,true"));

            // Invalid registration date
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,AVAILABLE,28.4,,2020-12-01,21,FIXED_GEAR,true"));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,AVAILABLE,28.4,INVALID_DATE,2020-12-01,21,FIXED_GEAR,true"));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,AVAILABLE,28.4,2150-01-13,2200-12-01,21,FIXED_GEAR,true"));

            // Invalid last maintenance date
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,AVAILABLE,28.4,2020-01-13,,21,FIXED_GEAR,true"));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,AVAILABLE,28.4,2020-01-13,INVALID_DATE,21,FIXED_GEAR,true"));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-001,AVAILABLE,28.4,2020-01-13,2019-12-01,21,FIXED_GEAR,true"));

            // Invalid gear count
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-002,AVAILABLE,28.4,2020-01-13,2020-12-01,0,FIXED_GEAR,true"));
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-003,AVAILABLE,28.4,2020-01-13,2020-12-01,-1,FIXED_GEAR,true"));

            // Invalid gear type
            assertThrows(Exception.class, () -> controller.addBicycle("MECHANICAL", "Bike-004,AVAILABLE,28.4,2020-01-13,2020-12-01,21,INVALID_GEAR,true"));

            // Invalid battery
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,0,52,9.7,500,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,-1,52,9.7,500,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,540,0,9.7,500,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,540,-1,9.7,500,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,0.0,500,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,-0.1,500,34.1,false"));

            // Invalid motor
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,9.7,-1,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,9.7,0,34.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,9.7,500,-0.1,false"));
            assertThrows(Exception.class, () -> controller.addBicycle("ELECTRICAL", "Bike-999,AVAILABLE,28.4,2020-01-13,2020-12-01,540,52,9.7,500,0.0,false"));
        } catch (Exception e) {
            System.out.println("Error in testAddBicycleInvalidArguments: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @Tag("basic")
    @DisplayName("Basic - Assign Bicycles to Stations")
    public void testAssignBicyclesToStations() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            for (int i = 0; i < controller.getStations().length; i++) {
                Gson gson = new GsonBuilder().create();
                String stationId = gson.fromJson(JsonParser.parseString(STATIONS_EXPECTED[i]), JsonObject.class).get("id").getAsString();
                Object[] bicycles = controller.getBicyclesByStation(stationId);
                assertEquals(STATION_BICYCLES_EXPECTED[i].length, bicycles.length, "Station " + stationId + " should have expected number of bicycles");

                for (int j = 0; j < bicycles.length; j++) {
                    String prettyJson = bicycles[j].toString();
                    JsonElement parsed = JsonParser.parseString(prettyJson);
                    String bicycleJson = gson.toJson(parsed);

                    assertEquals(STATION_BICYCLES_EXPECTED[i][j], bicycleJson, "Bicycle " + (j + 1) + " in Station " + stationId + " should match expected");
                }
            }
        } catch (Exception e) {
            System.out.println("Error in testAssignBicyclesToStations: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    @Tag("basic")
    @DisplayName("Basic - Assign Invalid Bicycle Station")
    public void testAssignBicycleToFullStation() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            // Non-existent Station
            assertThrows(Exception.class, () -> {
                controller.addBicycleToStation("Bike-124", "NonExistentStation");
            });

            // Non-existent Bicycle
            assertThrows(Exception.class, () -> {
                controller.addBicycleToStation("NonExistentBike", "04c1f418a16200483f9ff8d36ffbea54");
            });

            // Bicycle already exists in station
            assertThrows(Exception.class, () -> {
                controller.addBicycleToStation("Bike-124", "04c1f418a16200483f9ff8d36ffbea54");
            });

            // Station full
            for (int i = 9; i < 16; i++) {
                controller.addBicycle("MECHANICAL", "Bike-Full-" + i + ",AVAILABLE,28.4,2020-01-13,2020-12-01,21,FIXED_GEAR,true");
                controller.addBicycleToStation("Bike-Full-" + i, "04c1f418a16200483f9ff8d36ffbea54");
            }

            controller.addBicycle("MECHANICAL", "Bike-Overflow,AVAILABLE,28.4,2020-01-13,2020-12-01,21,FIXED_GEAR,true");
            assertThrows(Exception.class, () -> {
                controller.addBicycleToStation("Bike-Overflow", "04c1f418a16200483f9ff8d36ffbea54");
            });
        } catch (Exception e) {
            fail("Error in testAssignBicycleToFullStation: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    @Tag("advanced")
    @DisplayName("Advanced - Add user")
    public void testAddUser() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            assertThrows(Exception.class, () -> controller.createUser(null));
            assertThrows(Exception.class, () -> controller.createUser(""));
            assertThrows(Exception.class, () -> controller.createUser("   "));

            controller.createUser("User1");
            String userName = controller.getUserName();
            assertEquals("User1", userName, "User name should be 'User1'");
        } catch (Exception e) {
            System.out.println("Error in testAddUser: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    @Tag("advanced")
    @DisplayName("Advanced - Start Trip")
    public void testStartTrip() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            controller.createUser("User1");

            LocalDateTime before = LocalDateTime.now();
            controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-124");
            LocalDateTime after = LocalDateTime.now();

            JsonObject actual = JsonParser
                    .parseString(controller.getCurrentTrip())
                    .getAsJsonObject();

            assertTrue(actual.has("startTime"), "Current trip should contain startTime");

            assertFalse(actual.has("endStation"), "Current trip should not contain endStation");
            assertFalse(actual.has("endTime"), "Current trip should not contain endTime");

            LocalDateTime startTime =
                    LocalDateTime.parse(actual.get("startTime").getAsString());

            assertFalse(startTime.isBefore(before.minusSeconds(1)),
                    "startTime should be >= before - 1s");
            assertFalse(startTime.isAfter(after.plusSeconds(1)),
                    "startTime should be <= after + 1s");

            actual.remove("startTime");

            JsonObject expected = JsonParser.parseString(
                    "{"
                            + "\"bicycle\":{"
                            + "\"id\":\"Bike-124\","
                            + "\"status\":\"Available\","
                            + "\"weight\":19.6,"
                            + "\"registrationDate\":\"2021-05-08\","
                            + "\"lastMaintenanceDate\":\"2023-06-11\","
                            + "\"type\":\"MECHANICAL\","
                            + "\"mechanical\":{"
                            + "\"gearCount\":1,"
                            + "\"gearType\":\"Fixed Gear\","
                            + "\"hasBasket\":true"
                            + "}"
                            + "},"
                            + "\"startStation\":{"
                            + "\"id\":\"04c1f418a16200483f9ff8d36ffbea54\","
                            + "\"location\":{"
                            + "\"latitude\":41.382131,"
                            + "\"longitude\":2.160653,"
                            + "\"address\":\"C/ Villarroel, 39\""
                            + "},"
                            + "\"MAX_CAPACITY\":16"
                            + "},"
                            + "\"distance\":0.0"
                            + "}"
            ).getAsJsonObject();

            assertEquals(expected, actual, "Current trip should match expected JSON except startTime");
        } catch (Exception e) {
            fail("Error in testStartTrip: " + e.getMessage());
        }
    }

    @Test
    @Order(13)
    @Tag("advanced")
    @DisplayName("Advanced - Invalid Start Trip")
    public void testStartTripInvalid() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            // Non-existent User
            assertThrows(Exception.class, () -> {
                controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-124");
            });

            controller.createUser("User1");

            // Non-existent Station
            assertThrows(Exception.class, () -> {
                controller.startTrip("NonExistentStation", "Bike-124");
            });

            // Non-existent Bicycle
            assertThrows(Exception.class, () -> {
                controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "NonExistentBike");
            });

            // Bicycle not in station
            assertThrows(Exception.class, () -> {
                controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-125");
            });

            // Bicycle not available
            controller.addBicycle("MECHANICAL", "Bike-Unavailable,MAINTENANCE,28.4,2020-01-13,2020-12-01,21,FIXED_GEAR,true");
            controller.addBicycleToStation("Bike-Unavailable", "04c1f418a16200483f9ff8d36ffbea54");
            assertThrows(Exception.class, () -> {
                controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-Unavailable");
            });

            // Start trip when already on a trip
            controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-124");
            assertThrows(Exception.class, () -> {
                controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-126");
            });
        } catch (Exception e) {
            fail("Error in testStartTripInvalid: " + e.getMessage());
        }
    }

    @Test
    @Order(14)
    @Tag("advanced")
    @DisplayName("Advanced - End Trip")
    public void testEndTrip() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            controller.createUser("User1");
            controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-124");

            LocalDateTime before = LocalDateTime.now();
            JsonObject actual = JsonParser
                    .parseString(controller.endTrip("09257dd0300decf3aa34145b7988dbba"))
                    .getAsJsonObject();
            LocalDateTime after = LocalDateTime.now();

            assertTrue(actual.has("endTime"), "Current trip should contain endTime");

            LocalDateTime endTime =
                    LocalDateTime.parse(actual.get("endTime").getAsString());

            assertFalse(endTime.isBefore(before.minusSeconds(1)),
                    "endTime should be >= before - 1s");
            assertFalse(endTime.isAfter(after.plusSeconds(1)),
                    "endTime should be <= after + 1s");

            actual.remove("startTime");
            actual.remove("endTime");

            JsonObject expected = JsonParser.parseString(
                    "{"
                            + "\"bicycle\":{"
                            + "\"id\":\"Bike-124\","
                            + "\"status\":\"Available\","
                            + "\"weight\":19.6,"
                            + "\"registrationDate\":\"2021-05-08\","
                            + "\"lastMaintenanceDate\":\"2023-06-11\","
                            + "\"type\":\"MECHANICAL\","
                            + "\"mechanical\":{"
                            + "\"gearCount\":1,"
                            + "\"gearType\":\"Fixed Gear\","
                            + "\"hasBasket\":true"
                            + "}"
                            + "},"
                            + "\"startStation\":{"
                            + "\"id\":\"04c1f418a16200483f9ff8d36ffbea54\","
                            + "\"location\":{"
                            + "\"latitude\":41.382131,"
                            + "\"longitude\":2.160653,"
                            + "\"address\":\"C/ Villarroel, 39\""
                            + "},"
                            + "\"MAX_CAPACITY\":16"
                            + "},"
                            + "\"endStation\":{"
                            + "\"id\":\"09257dd0300decf3aa34145b7988dbba\","
                            + "\"location\":{"
                            + "\"latitude\":41.414561,"
                            + "\"longitude\":2.165868,"
                            + "\"address\":\"C/ Camèlies, 78\""
                            + "},"
                            + "\"MAX_CAPACITY\":20"
                            + "},"
                            + "\"distance\":3.63"
                            + "}"
            ).getAsJsonObject();

            assertEquals(expected, actual, "Current trip should match expected JSON except startTime and endTime");
        } catch (Exception e) {
            fail("Error in testEndTrip: " + e.getMessage());
        }
    }

    @Test
    @Order(15)
    @Tag("advanced")
    @DisplayName("Advanced - Invalid End Trip")
    public void testEndTripInvalid() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            // Non-existent User
            assertThrows(Exception.class, () -> {
                controller.endTrip("09257dd0300decf3aa34145b7988dbba");
            });

            controller.createUser("User1");

            // End trip when not on a trip
            assertThrows(Exception.class, () -> {
                controller.endTrip("09257dd0300decf3aa34145b7988dbba");
            });

            controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-124");

            // Non-existent Station
            assertThrows(Exception.class, () -> {
                controller.endTrip("NonExistentStation");
            });

            // Station full
            for (int i = 0; i < 12; i++) {
                controller.addBicycle("MECHANICAL", "Bike-FullEnd-" + i + ",AVAILABLE,28.4,2020-01-13,2020-12-01,21,FIXED_GEAR,true");
                controller.addBicycleToStation("Bike-FullEnd-" + i, "09257dd0300decf3aa34145b7988dbba");
            }
            assertThrows(Exception.class, () -> controller.endTrip("09257dd0300decf3aa34145b7988dbba"));
        } catch (Exception e) {
            fail("Error in testEndTripInvalid: " + e.getMessage());
        }
    }

    @Test
    @Order(16)
    @Tag("advanced")
    @DisplayName("Advanced - User Trip History")
    public void testUserTripHistory() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            controller.createUser("User1");

            // Empty trip history
            String[] emptyTripHistoryJson = controller.getTrips();
            assertEquals(0, emptyTripHistoryJson.length, "User should have 0 trips in history");

            controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-124");
            controller.endTrip("09257dd0300decf3aa34145b7988dbba");

            controller.startTrip("09257dd0300decf3aa34145b7988dbba", "Bike-044");
            controller.endTrip("04c1f418a16200483f9ff8d36ffbea54");

            String[] tripHistoryJson = controller.getTrips();
            assertEquals(2, tripHistoryJson.length, "User should have 2 trips in history");
            for (int i = 0; i < tripHistoryJson.length; i++) {
                JsonObject actual = JsonParser
                        .parseString(tripHistoryJson[i])
                        .getAsJsonObject();

                assertTrue(actual.has("startTime"), "Trip should contain startTime");
                assertTrue(actual.has("endTime"), "Trip should contain endTime");

                actual.remove("startTime");
                actual.remove("endTime");

                JsonObject expected = JsonParser.parseString(
                        TRIP_HISTORY_EXPECTED[i]
                ).getAsJsonObject();

                assertEquals(expected, actual, "Trip " + (i + 1) + " should match expected JSON except startTime and endTime");
            }
        } catch (Exception e) {
            fail("Error in testUserTripHistory: " + e.getMessage());
        }
    }

    @Test
    @Order(17)
    @Tag("advanced")
    @DisplayName("Advanced - Add and Remove Bicycle During Trip")
    public void testAddRemoveBicycleDuringTrip() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            controller.createUser("User1");

            controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-124");

            // Check if the station no longer has the bicycle
            Object[] bicyclesAtStartStation = controller.getBicyclesByStation("04c1f418a16200483f9ff8d36ffbea54");
            for (Object bike : bicyclesAtStartStation) {
                JsonObject bikeJson = JsonParser.parseString(bike.toString()).getAsJsonObject();
                assertNotEquals("Bike-124", bikeJson.get("id").getAsString(), "Bike-124 should not be at the start station during the trip");
            }

            controller.endTrip("09257dd0300decf3aa34145b7988dbba");

            // Check if the station now has the bicycle
            Object[] bicyclesAtEndStation = controller.getBicyclesByStation("09257dd0300decf3aa34145b7988dbba");
            boolean found = false;
            for (Object bike : bicyclesAtEndStation) {
                JsonObject bikeJson = JsonParser.parseString(bike.toString()).getAsJsonObject();
                if (bikeJson.get("id").getAsString().equals("Bike-124")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Bike-124 should be at the end station after the trip");

            // Now back to start station
            controller.startTrip("09257dd0300decf3aa34145b7988dbba", "Bike-124");
            controller.endTrip("04c1f418a16200483f9ff8d36ffbea54");

            Object[] bicyclesAtStartStationAfterReturn = controller.getBicyclesByStation("04c1f418a16200483f9ff8d36ffbea54");
            found = false;
            for (Object bike : bicyclesAtStartStationAfterReturn) {
                JsonObject bikeJson = JsonParser.parseString(bike.toString()).getAsJsonObject();
                if (bikeJson.get("id").getAsString().equals("Bike-124")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Bike-124 should be back at the start station after returning the trip");
        } catch (Exception e) {
            fail("Error in testAddRemoveBicycleDuringTrip: " + e.getMessage());
        }
    }

    @Test
    @Order(18)
    @Tag("advanced")
    @DisplayName("Advanced - Empty Station Handling")
    public void testEmptyStationHandling() {
        try {
            UOCycleController controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");

            controller.createUser("User1");

            // Remove all bicycles from the station
            Object[] bicyclesAtStation = controller.getBicyclesByStation("04c1f418a16200483f9ff8d36ffbea54");
            for (Object bike : bicyclesAtStation) {
                JsonObject bikeJson = JsonParser.parseString(bike.toString()).getAsJsonObject();
                String bikeId = bikeJson.get("id").getAsString();

                controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", bikeId);
                controller.endTrip("09257dd0300decf3aa34145b7988dbba");
            }

            // Now the station should be empty
            Object[] bicyclesAfterRemoval = controller.getBicyclesByStation("04c1f418a16200483f9ff8d36ffbea54");
            assertEquals(0, bicyclesAfterRemoval.length, "Station should be empty after removing all bicycles");
            assertThrows(Exception.class, () -> controller.startTrip("04c1f418a16200483f9ff8d36ffbea54", "Bike-126"));
        } catch (Exception e) {
            fail("Error in testEmptyStationHandling: " + e.getMessage());
        }
    }

}
