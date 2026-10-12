import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Command-line menu. This is the ONLY class that reads input or prints output.
 */
public class Main {
    private static final String RIDERS_FILE = "riders_100.csv";
    private static final String DRIVERS_FILE = "drivers_30.csv";
    private static final String RIDES_FILE = "rides_40.csv";

    public static void main(String[] args) {
        RideSharingSystem system = new RideSharingSystem();
        Scanner sc = new Scanner(System.in);

        // Startup load: riders and drivers first, because rides refer to them
        boolean ridersOk = system.loadRidersFromCSV(RIDERS_FILE);
        boolean driversOk = system.loadDriversFromCSV(DRIVERS_FILE);
        boolean ridesOk = system.loadRidesFromCSV(RIDES_FILE);
        System.out.println("Loaded riders: " + (ridersOk ? "OK" : "FAILED")
                + " | drivers: " + (driversOk ? "OK" : "FAILED")
                + " | rides: " + (ridesOk ? "OK" : "FAILED"));

        try {
            int choice = -1;
            while (choice != 0) {
                printMenu();
                choice = readInt(sc, "Choose an option: ");
                System.out.println();
                switch (choice) {
                    case 1: listAllRiders(system); break;
                    case 2: searchRidersByCity(system, sc); break;
                    case 3: searchRidersByName(system, sc); break;
                    case 4: addRiderMenu(system, sc); break;
                    case 5: listAllDrivers(system); break;
                    case 6: searchDriversByType(system, sc); break;
                    case 7: addDriverMenu(system, sc); break;
                    case 8: listAllRides(system); break;
                    case 9: searchRidesByPickup(system, sc); break;
                    case 10: searchRidesByRider(system, sc); break;
                    case 11: showSharedParticipants(system, sc); break;
                    case 12: addRideMenu(system, sc); break;
                    case 0: System.out.println("Goodbye!"); break;
                    default: System.out.println("Invalid option, please choose from the menu.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Input closed. Goodbye!");   // end of input (no more lines)
        }
    }

    // ---------------------------------------------------------------- menu and helpers

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Ride-Sharing System =====");
        System.out.println("1.  List all riders");
        System.out.println("2.  Search for riders by home city");
        System.out.println("3.  Search for riders by name");
        System.out.println("4.  Add a rider");
        System.out.println("5.  List all drivers");
        System.out.println("6.  Search for drivers by vehicle type");
        System.out.println("7.  Add a driver");
        System.out.println("8.  List all rides alphabetically by pickup location");
        System.out.println("9.  Search for rides by pickup location");
        System.out.println("10. Search for rides by rider name");
        System.out.println("11. List the participants of a shared ride by pickup location");
        System.out.println("12. Add a ride (private or shared)");
        System.out.println("0.  Exit the program");
    }

    // Prints every element on its own line, or a clear message when the list is empty
    private static <T> void printAll(LinkedList<T> list) {
        if (list.empty()) {
            System.out.println("No results found.");
            return;
        }
        list.findFirst();
        while (true) {
            System.out.println(list.retrieve().toString());
            if (list.last())
                break;
            list.findNext();
        }
    }

    // Reads a whole line (trimmed)
    private static String readLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    // Keeps asking until the user types a valid integer
    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            String text = readLine(sc, prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    // Shows the vehicle types as a numbered list and returns the chosen one
    private static VehicleType chooseVehicleType(Scanner sc) {
        VehicleType[] types = VehicleType.values();
        for (int i = 0; i < types.length; i++)
            System.out.println((i + 1) + ". " + types[i]);
        while (true) {
            int n = readInt(sc, "Choose vehicle type (number): ");
            if (n >= 1 && n <= types.length)
                return types[n - 1];
            System.out.println("Please choose a number from the list.");
        }
    }

    // Prints success or failure of an add operation
    private static void printResult(boolean ok, String successText, String failText) {
        System.out.println(ok ? successText : failText);
    }

    // ---------------------------------------------------------------- riders (options 1-4)

    private static void listAllRiders(RideSharingSystem system) {
        printAll(system.getAllRiders());
    }

    private static void searchRidersByCity(RideSharingSystem system, Scanner sc) {
        printAll(system.searchRidersByHomeCity(readLine(sc, "Home city: ")));
    }

    private static void searchRidersByName(RideSharingSystem system, Scanner sc) {
        printAll(system.searchRidersByName(readLine(sc, "Full name: ")));
    }

    private static void addRiderMenu(RideSharingSystem system, Scanner sc) {
        int id = readInt(sc, "Rider ID: ");
        String name = readLine(sc, "Full name: ");
        String email = readLine(sc, "Email: ");
        String phone = readLine(sc, "Phone (10 digits): ");
        String city = readLine(sc, "Home city: ");
        try {
            boolean ok = system.addRider(new Rider(id, name, email, phone, city));
            printResult(ok, "Rider added successfully.", "Failed: a rider with this ID already exists.");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- drivers (options 5-7)

    private static void listAllDrivers(RideSharingSystem system) {
        printAll(system.getAllDrivers());
    }

    private static void searchDriversByType(RideSharingSystem system, Scanner sc) {
        printAll(system.searchDriversByVehicleType(chooseVehicleType(sc)));
    }

    private static void addDriverMenu(RideSharingSystem system, Scanner sc) {
        int id = readInt(sc, "Driver ID: ");
        String name = readLine(sc, "Full name: ");
        String phone = readLine(sc, "Phone (10 digits): ");
        String plate = readLine(sc, "Vehicle plate (3 capital letters + 4 digits): ");
        VehicleType type = chooseVehicleType(sc);
        try {
            boolean ok = system.addDriver(new Driver(id, name, phone, plate, type));
            printResult(ok, "Driver added successfully.",
                    "Failed: the driver ID or the vehicle plate already exists.");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- rides (options 8-12)

    private static void listAllRides(RideSharingSystem system) {
        printAll(system.getAllRidesAlphabetically());
    }

    private static void searchRidesByPickup(RideSharingSystem system, Scanner sc) {
        printAll(system.searchRidesByPickupLocation(readLine(sc, "Pickup location: ")));
    }

    private static void searchRidesByRider(RideSharingSystem system, Scanner sc) {
        printAll(system.searchRidesByRiderName(readLine(sc, "Rider full name: ")));
    }

    private static void showSharedParticipants(RideSharingSystem system, Scanner sc) {
        printAll(system.getSharedRideParticipants(readLine(sc, "Pickup location of the shared ride: ")));
    }

    private static void addRideMenu(RideSharingSystem system, Scanner sc) {
        int type = 0;
        while (type != 1 && type != 2) {
            System.out.println("1. Private ride");
            System.out.println("2. Shared ride");
            type = readInt(sc, "Ride type: ");
        }
        String pickup = readLine(sc, "Pickup location: ");
        String dropoff = readLine(sc, "Drop-off location: ");
        DateTime pickupTime;
        DateTime dropoffTime;
        try {
            pickupTime = DateTime.parse(readLine(sc, "Pickup date/time (MM/DD/YYYY HH:MM): "));
            dropoffTime = DateTime.parse(readLine(sc, "Drop-off date/time (MM/DD/YYYY HH:MM): "));
        } catch (IllegalArgumentException e) {
            System.out.println("Failed: invalid date/time. Use MM/DD/YYYY HH:MM.");
            return;
        }
        int driverId = readInt(sc, "Driver ID: ");
        String failText = "Failed: check that the rider(s) and driver exist, the times are valid "
                + "and the ride does not overlap another ride.";
        if (type == 1) {
            int riderId = readInt(sc, "Rider ID: ");
            printResult(system.schedulePrivateRide(pickup, pickupTime, dropoffTime, dropoff, riderId, driverId),
                    "Private ride scheduled successfully.", failText);
        } else {
            int count = 0;
            while (count < 2)
                count = readInt(sc, "Number of riders (at least 2): ");
            int[] riderIds = new int[count];
            for (int i = 0; i < count; i++)
                riderIds[i] = readInt(sc, "Rider ID #" + (i + 1) + ": ");
            printResult(system.scheduleSharedRide(pickup, pickupTime, dropoffTime, dropoff, riderIds, driverId),
                    "Shared ride scheduled successfully.", failText);
        }
    }
}
