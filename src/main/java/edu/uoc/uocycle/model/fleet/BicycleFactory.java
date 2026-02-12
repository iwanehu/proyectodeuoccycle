package edu.uoc.uocycle.model.fleet;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class BicycleFactory {

    // Prevent instantiation
    private BicycleFactory() {}

    public static Bicycle create(String type, String args) throws BicycleException {
        if (type == null) {
            throw new BicycleException(BicycleException.INVALID_TYPE);
        }

        try {
            return switch (type) {
                case "MECHANICAL" -> createMechanicalBicycle(args);
                case "ELECTRICAL" -> createElectricBicycle(args);
                default -> throw new BicycleException(BicycleException.INVALID_TYPE);
            };
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new BicycleException(e.getMessage());
        }
    }

    private static String[] parseArgs(String args, int expectedLength) throws BicycleException {
        if (args == null) {
            throw new BicycleException(BicycleException.INVALID_ARGUMENTS);
        }

        String[] params = args.split(",", -1);

        if (params.length != expectedLength) {
            throw new BicycleException(BicycleException.INVALID_ARGUMENTS);
        }

        return params;
    }

    private static Bicycle createMechanicalBicycle(String args) throws BicycleException {
        String[] params = parseArgs(args, 8);

        return new MechanicalBicycle(
                params[0],
                BicycleStatus.valueOf(params[1]),
                Double.parseDouble(params[2]),
                LocalDate.parse(params[3]),
                LocalDate.parse(params[4]),
                Integer.parseInt(params[5]),
                GearType.valueOf(params[6]),
                Boolean.parseBoolean(params[7])
        );
    }

    private static Bicycle createElectricBicycle(String args) throws BicycleException {
        String[] params = parseArgs(args, 11);

        return new ElectricBicycle(
                params[0],
                BicycleStatus.valueOf(params[1]),
                Double.parseDouble(params[2]),
                LocalDate.parse(params[3]),
                LocalDate.parse(params[4]),
                Integer.parseInt(params[5]),
                Integer.parseInt(params[6]),
                Double.parseDouble(params[7]),
                Integer.parseInt(params[8]),
                Double.parseDouble(params[9]),
                Boolean.parseBoolean(params[10])
        );
    }

}
