import java.util.Scanner;

/**
 * Main.java - Command-line interface for the Ride-Sharing System.
 *
 * This is the ONLY class that reads from the keyboard or prints to the screen.
 * All other classes just return values, and Main decides how to display them.
 *
 * At startup it loads the three CSV files so the menu always has real data.
 * Then it shows the numbered menu until the user chooses 0 (exit).
 *
 * Menu options assigned to this student: 1, 2, 3, 4 (rider operations).
 * The remaining options are handled by teammates.
 */
public class Main {

    public static void main(String[] args) {

        // Step 1: create the system and load the CSV files.
        // Time for loadRidersFromCSV is O(m^2) where m = number of lines
        // (each addRider is O(n) and n grows with each insertion).
        RideSharingSystem system = new RideSharingSystem();
        if (!system.loadRidersFromCSV("riders_100.csv"))
            System.out.println("Warning: could not open riders_100.csv.");
        if (!system.loadDriversFromCSV("drivers_30.csv"))
            System.out.println("Warning: could not open drivers_30.csv.");
        if (!system.loadRidesFromCSV("rides_40.csv"))
            System.out.println("Warning: could not open rides_40.csv.");

        // Step 2: prepare the input reader and the choice variable.
        Scanner input = new Scanner(System.in);
        int choice = -1;

        // Step 3: main loop - keep showing the menu until the user picks 0.
        while (choice != 0) {
            printMenu();
            System.out.print("Enter your choice: ");

            // Read the whole line and convert it to an int.
            // We use nextLine() to avoid the common bug of nextInt()
            // leaving a newline in the buffer.
            try {
                choice = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            // Step 4: call the matching operation.
            switch (choice) {
                case 1:
                    listAllRiders(system);
                    break;
                case 2:
                    searchRidersByHomeCity(system, input);
                    break;
                case 3:
                    searchRidersByName(system, input);
                    break;
                case 4:
                    addRider(system, input);
                    break;
                case 5:
                    listAllDrivers(system);
                    break;
                case 6:
                    searchDriversByVehicleType(system, input);
                    break;
                case 7:
                    addDriver(system, input);
                    break;
                case 8:
                    // TODO: teammate - list rides alphabetically
                    listAllRides(system);
                    break;
                case 9:
                    // TODO: teammate - search rides by pickup location
                    searchRidesByPickupLocation(system, input);
                    break;
                case 10:
                    // TODO: teammate - search rides by rider name
                    searchRidesByRiderName(system, input);
                    break;
                case 11:
                    // TODO: teammate - shared ride participants
                    listSharedRideParticipants(system, input);
                    break;
                case 12:
                    // TODO: teammate - add a ride
                    addRide(system, input);
                    break;
                case 0:
                    System.out.println("Exiting. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        input.close();
    }

    /**
     * Prints the numbered menu exactly as required by Section 4.
     * Time: O(1).
     */
    private static void printMenu() {
        System.out.println();
        System.out.println("========== Ride-Sharing System ==========");
        System.out.println("1.  List all riders.");
        System.out.println("2.  Search for riders by home city.");
        System.out.println("3.  Search for riders by name.");
        System.out.println("4.  Add a rider.");
        System.out.println("5.  List all drivers.");
        System.out.println("6.  Search for drivers by vehicle type.");
        System.out.println("7.  Add a driver.");
        System.out.println("8.  List all rides alphabetically by pickup location.");
        System.out.println("9.  Search for rides by pickup location.");
        System.out.println("10. Search for rides by rider name.");
        System.out.println("11. List the participants of a shared ride by pickup location.");
        System.out.println("12. Add a ride.");
        System.out.println("0.  Exit the program.");
        System.out.println("=========================================");
    }

    /**
     * Option 1: list all riders in the system, one per line.
     * Time: O(n) where n = number of riders.
     */
    private static void listAllRiders(RideSharingSystem system) {
        printRiders(system.getAllRiders(), "No riders in the system.");
    }

    /**
     * Option 2: ask the user for a city, then print every rider from that city.
     * Time: O(n) where n = number of riders.
     */
    private static void searchRidersByHomeCity(RideSharingSystem system, Scanner input) {
        System.out.print("Enter home city: ");
        String city = input.nextLine().trim();

        LinkedList<IRider> results = system.searchRidersByHomeCity(city);
        printRiders(results, "No riders found in that city.");
    }

    /**
     * Option 3: ask the user for a full name, then print every matching rider.
     * Time: O(n) where n = number of riders.
     */
    private static void searchRidersByName(RideSharingSystem system, Scanner input) {
        System.out.print("Enter full name: ");
        String name = input.nextLine().trim();

        LinkedList<IRider> results = system.searchRidersByName(name);
        printRiders(results, "No riders found with that name.");
    }

    /**
     * Option 4: add a new rider. We collect every field from the user,
     * build the Rider object, and call addRider.
     * If the input is invalid (bad phone, non-numeric ID, duplicate ID),
     * we print an error and return to the menu.
     * Time: O(n) for the underlying add.
     */
    private static void addRider(RideSharingSystem system, Scanner input) {
        try {
            System.out.print("Enter rider ID: ");
            int id = Integer.parseInt(input.nextLine().trim());

            System.out.print("Enter full name: ");
            String name = input.nextLine().trim();

            System.out.print("Enter email: ");
            String email = input.nextLine().trim();

            System.out.print("Enter phone number (10 digits): ");
            String phone = input.nextLine().trim();

            System.out.print("Enter home city: ");
            String city = input.nextLine().trim();

            // The Rider constructor validates the phone number format
            // and throws IllegalArgumentException if it is not exactly
            // 10 digits.
            IRider newRider = new Rider(id, name, email, phone, city);

            if (system.addRider(newRider)) {
                System.out.println("Rider added successfully.");
            } else {
                System.out.println("Failed: a rider with this ID already exists.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Failed: the ID must be a whole number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    /**
     * Helper: print a LinkedList<IRider>, one rider per line.
     * If the list is empty, print the given message instead.
     * Time: O(k) where k = size of the list.
     */
    private static void printRiders(LinkedList<IRider> list, String emptyMessage) {
        if (list.empty()) {
            System.out.println(emptyMessage);
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
    //Option 5: list all drivers in the system, one per line.
    private static void listAllDrivers(RideSharingSystem system) {
        printDrivers(system.getAllDrivers(), "No drivers in the system.");
    }
//Option 6: ask the user for a vehicle type, then print every driver with that type. An unknown type prints an error instead.
    private static void searchDriversByVehicleType(RideSharingSystem system, Scanner input) {
        System.out.print("Enter vehicle type (SEDAN, LUXURY_SEDAN, SUV, VAN): ");
        String text = input.nextLine();
 
        try {
            VehicleType type = parseVehicleType(text);
            LinkedList<IDriver> results = system.searchDriversByVehicleType(type);
            printDrivers(results, "No drivers found with that vehicle type.");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
/*  Option 7: add a new driver. We collect every field from the user,
      build the Driver object, and call addDriver.
      If the input is invalid (non-numeric ID, bad phone, bad plate,
      unknown vehicle type) or the ID or plate already exists, we print
      a clear message and return to the menu.*/
      
    private static void addDriver(RideSharingSystem system, Scanner input) {
        try {
            System.out.print("Enter driver ID: ");
            int id = Integer.parseInt(input.nextLine().trim());
 
            System.out.print("Enter full name: ");
            String name = input.nextLine().trim();
 
            System.out.print("Enter phone number (10 digits): ");
            String phone = input.nextLine().trim();
 
            System.out.print("Enter vehicle plate (3 uppercase letters + 4 digits, e.g. ABC1234): ");
            String plate = input.nextLine().trim();
 
            System.out.print("Enter vehicle type (SEDAN, LUXURY_SEDAN, SUV, VAN): ");
            VehicleType type = parseVehicleType(input.nextLine());
 
            // The Driver constructor validates the phone number and the plate
            // and throws IllegalArgumentException if either one is invalid.
            IDriver newDriver = new Driver(id, name, phone, plate, type);
 
            if (system.addDriver(newDriver)) {
                System.out.println("Driver added successfully.");
            } else {
                System.out.println("Failed: a driver with this ID or vehicle plate already exists.");
            }
 
        } catch (NumberFormatException e) {
            // NumberFormatException is a kind of IllegalArgumentException,
            // so this catch must come first.
            System.out.println("Failed: the ID must be a whole number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    
    /* Helper: turn the user's text into a VehicleType.
       Accepts "SUV", "suv", "luxury sedan", "LUXURY_SEDAN"
       Throws IllegalArgumentException if the text is not a valid type.*/
    private static VehicleType parseVehicleType(String text) {
        String normalized = text.trim().toUpperCase().replace(' ', '_').replace('-', '_');
        try {
            return VehicleType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid vehicle type. Choose SEDAN, LUXURY_SEDAN, SUV or VAN.");
        }
    }
   
   
    /*Helper: print a LinkedList<IDriver>, one driver per line.
      If the list is empty, print the given message instead*/
  private static void printDrivers(LinkedList<IDriver> list, String emptyMessage) {
        if (list.empty()) {
            System.out.println(emptyMessage);
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
    /**
     * Helper: print a LinkedList<IRide>, one ride per line.
     * If the list is empty, print the given message instead.
     * Time: O(n * k) because a shared ride's toString lists its k riders.
     */
    private static void printRides(LinkedList<IRide> list, String emptyMessage) {
        if (list.empty()) {
            System.out.println(emptyMessage);
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
}
