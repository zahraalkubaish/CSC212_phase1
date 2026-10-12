import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * RideSharingSystem: coordinates riders, drivers, and rides.
 * It stores the three lists, enforces uniqueness of IDs and plates,
 * and performs cascade removal when a rider or driver is removed.
 * This class does not print anything - every search returns its result.
 */
public class RideSharingSystem implements IRideSharingSystem {

    private RiderList riderList;
    private DriverList driverList;
    private RideList rideList;

    public RideSharingSystem() {
        riderList = new RiderList();
        driverList = new DriverList();
        rideList = new RideList();
    }

    // -------- CSV loading --------

    /**
     * Loads riders from a CSV file.
     * Line format: riderId,name,email,phoneNumber,homeCity
     * Bad lines are skipped (wrong number of fields, invalid phone, etc.).
     * Returns false if the path is null or the file cannot be opened.
     * Complexity: O(m*n) where m = lines and n = riders so far.
     */
    public boolean loadRidersFromCSV(String ridersFilePath) {
        if (ridersFilePath == null)
            return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(ridersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.trim().split(",", -1);
                if (f.length != 5)
                    continue;
                try {
                    int id = Integer.parseInt(f[0].trim());
                    String name = f[1].trim();
                    String email = f[2].trim();
                    String phone = f[3].trim();
                    String city = f[4].trim();
                    addRider(new Rider(id, name, email, phone, city));
                } catch (IllegalArgumentException e) {
                    // bad line: just skip it and keep loading
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Loads drivers from a CSV file.
     * Line format: driverId,name,phoneNumber,vehiclePlate,vehicleType
     * Returns false if the path is null or the file cannot be opened.
     * Complexity: O(m*n).
     */
    public boolean loadDriversFromCSV(String driversFilePath) {
        if (driversFilePath == null)
            return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(driversFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.trim().split(",", -1);
                if (f.length != 5)
                    continue;
                VehicleType type = parseVehicleType(f[4]);
                if (type == null)
                    continue;
                try {
                    int id = Integer.parseInt(f[0].trim());
                    String name = f[1].trim();
                    String phone = f[2].trim();
                    String plate = f[3].trim();
                    addDriver(new Driver(id, name, phone, plate, type));
                } catch (IllegalArgumentException e) {
                    // bad line: skip it
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Loads rides from a CSV file.
     * Line format: rideType,pickup,pickupDateTime,dropoffDateTime,dropoff,driverId,riderIds
     * Riders and driver must already be loaded.
     * Lines that fail any system rule are skipped.
     * Returns false if the path is null or the file cannot be opened.
     */
    public boolean loadRidesFromCSV(String ridesFilePath) {
        if (ridesFilePath == null)
            return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(ridesFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.trim().split(",", -1);
                if (f.length != 7)
                    continue;
                try {
                    String type = f[0].trim().toUpperCase();
                    String pickupLoc = f[1].trim();
                    DateTime pickup = DateTime.parse(f[2].trim());
                    DateTime dropoff = DateTime.parse(f[3].trim());
                    String dropoffLoc = f[4].trim();
                    int driverId = Integer.parseInt(f[5].trim());

                    // riderIds are separated by semicolons
                    String[] idTexts = f[6].trim().split(";");
                    int[] riderIds = new int[idTexts.length];
                    for (int i = 0; i < idTexts.length; i++)
                        riderIds[i] = Integer.parseInt(idTexts[i].trim());

                    if (type.equals("PRIVATE") && riderIds.length == 1)
                        schedulePrivateRide(pickupLoc, pickup, dropoff, dropoffLoc,
                                            riderIds[0], driverId);
                    else if (type.equals("SHARED"))
                        scheduleSharedRide(pickupLoc, pickup, dropoff, dropoffLoc,
                                           riderIds, driverId);
                } catch (IllegalArgumentException e) {
                    // bad line: skip it
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Turns text like "LUXURY SEDAN" or "luxury-sedan" into a VehicleType.
    // Returns null if the text does not match any type.
    private VehicleType parseVehicleType(String text) {
        if (text == null)
            return null;
        String normalized = text.trim().toUpperCase().replace(' ', '_').replace('-', '_');
        try {
            return VehicleType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // -------- riders --------

    // Adds a rider; fails if it is null or the ID is already used.
    public boolean addRider(IRider rider) {
        if (rider == null)
            return false;
        return riderList.add(rider);
    }

    // Returns the rider with this ID, or null if not found.
    public IRider searchRiderById(int riderId) {
        return riderList.findById(riderId);
    }

    // Returns the rider with this email, or null if not found.
    public IRider searchRiderByEmail(String email) {
        return riderList.findByEmail(email);
    }

    // Returns every rider whose full name matches exactly.
    public LinkedList<IRider> searchRidersByName(String fullName) {
        return riderList.findByName(fullName);
    }

    // Returns every rider living in this home city.
    public LinkedList<IRider> searchRidersByHomeCity(String homeCity) {
        return riderList.findByHomeCity(homeCity);
    }

    // Returns a new list with all riders, sorted by ID.
    public LinkedList<IRider> getAllRiders() {
        return riderList.getAll();
    }

    /**
     * Removes a rider and cleans up the rides they were involved in.
     * - Private rides of this rider are deleted completely.
     * - Shared rides lose this rider; if no participants remain, the ride is deleted.
     * Returns true if the rider existed.
     */
    public boolean removeRider(int riderId) {
        IRider rider = riderList.findById(riderId);
        if (rider == null)
            return false;

        // We make a copy of the history because deleteRide will modify it
        // while we are still iterating.
        LinkedList<IRide> rides = copyOf(rider.getRideHistory());

        if (!rides.empty()) {
            rides.findFirst();
            while (true) {
                IRide ride = rides.retrieve();
                if (ride instanceof ISharedRide) {
                    ISharedRide shared = (ISharedRide) ride;
                    shared.removeParticipantById(riderId);
                    // remove this ride from the rider's history too
                    removeFromHistory(rider.getRideHistory(), ride.getRideId());
                    if (shared.isEmpty())
                        deleteRide(ride);   // no one left, delete the whole ride
                } else {
                    // private ride: delete it entirely
                    deleteRide(ride);
                }
                if (rides.last())
                    break;
                rides.findNext();
            }
        }

        return riderList.removeById(riderId);
    }

    // -------- drivers --------

    // Adds a driver; fails on duplicate ID, duplicate plate, or null.
    public boolean addDriver(IDriver driver) {
        if (driver == null)
            return false;
        if (driverList.findByVehiclePlate(driver.getVehiclePlate()) != null)
            return false;
        return driverList.add(driver);
    }

    // Returns the driver with this ID, or null if not found.
    public IDriver searchDriverById(int driverId) {
        return driverList.findById(driverId);
    }

    // Returns the driver with this plate, or null if not found.
    public IDriver searchDriverByVehiclePlate(String vehiclePlate) {
        return driverList.findByVehiclePlate(vehiclePlate);
    }

    // Returns every driver with the given vehicle type.
    public LinkedList<IDriver> searchDriversByVehicleType(VehicleType vehicleType) {
        return driverList.findByVehicleType(vehicleType);
    }

    // Returns a new list with all drivers, sorted by ID.
    public LinkedList<IDriver> getAllDrivers() {
        return driverList.getAll();
    }

    /**
     * Removes a driver and deletes every ride assigned to them
     * (both private and shared). Returns true if the driver existed.
     */
    public boolean removeDriver(int driverId) {
        IDriver driver = driverList.findById(driverId);
        if (driver == null)
            return false;

        // copy the history so we can safely modify it while looping
        LinkedList<IRide> rides = copyOf(driver.getRideHistory());
        if (!rides.empty()) {
            rides.findFirst();
            while (true) {
                deleteRide(rides.retrieve());
                if (rides.last())
                    break;
                rides.findNext();
            }
        }
        return driverList.removeById(driverId);
    }

    // -------- scheduling --------

    /**
     * Schedules a private ride for one rider with one driver.
     * Fails if the rider or driver does not exist, if the times are invalid,
     * or if the time interval overlaps an existing ride for either one.
     */
    public boolean schedulePrivateRide(String pickupLocation, IDateTime pickupTime,
                                       IDateTime dropoffTime, String dropoffLocation,
                                       int riderId, int driverId) {
        IRider rider = riderList.findById(riderId);
        IDriver driver = driverList.findById(driverId);

        if (rider == null || driver == null || !validTimes(pickupTime, dropoffTime))
            return false;

        if (hasConflict(rider.getRideHistory(), pickupTime, dropoffTime)
                || hasConflict(driver.getRideHistory(), pickupTime, dropoffTime))
            return false;

        PrivateRide ride;
        try {
            ride = new PrivateRide(pickupLocation, pickupTime, dropoffTime,
                                   dropoffLocation, driver, rider);
        } catch (IllegalArgumentException e) {
            return false;
        }

        rideList.addRide(ride);
        rider.getRideHistory().insert(ride);
        driver.getRideHistory().insert(ride);
        return true;
    }

    /**
     * Schedules a shared ride for several riders with one driver.
     * Fails if any rider or the driver does not exist, if rider IDs are
     * duplicated in the request, or if any schedule would conflict.
     */
    public boolean scheduleSharedRide(String pickupLocation, IDateTime pickupTime,
                                      IDateTime dropoffTime, String dropoffLocation,
                                      int[] riderIds, int driverId) {
        if (riderIds == null || riderIds.length == 0 || !validTimes(pickupTime, dropoffTime))
            return false;

        IDriver driver = driverList.findById(driverId);
        if (driver == null || hasConflict(driver.getRideHistory(), pickupTime, dropoffTime))
            return false;

        // check every rider before we create anything
        for (int i = 0; i < riderIds.length; i++) {
            // reject a duplicate ID inside the same request
            for (int j = 0; j < i; j++) {
                if (riderIds[i] == riderIds[j])
                    return false;
            }
            IRider rider = riderList.findById(riderIds[i]);
            if (rider == null || hasConflict(rider.getRideHistory(), pickupTime, dropoffTime))
                return false;
        }

        SharedRide ride;
        try {
            ride = new SharedRide(pickupLocation, pickupTime, dropoffTime,
                                  dropoffLocation, driver);
        } catch (IllegalArgumentException e) {
            return false;
        }

        for (int i = 0; i < riderIds.length; i++) {
            IRider rider = riderList.findById(riderIds[i]);
            ride.addParticipant(rider);
            rider.getRideHistory().insert(ride);
        }
        rideList.addRide(ride);
        driver.getRideHistory().insert(ride);
        return true;
    }

    // -------- ride searches --------

    // Returns every ride whose pickup location matches.
    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation) {
        return rideList.findByPickupLocation(pickupLocation);
    }

    // Returns every ride that has a rider with this full name.
    public LinkedList<IRide> searchRidesByRiderName(String riderName) {
        return rideList.findByRiderName(riderName);
    }

    // Returns the distinct riders of every shared ride at this pickup location.
    public LinkedList<IRider> getSharedRideParticipants(String pickupLocation) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        LinkedList<IRide> rides = rideList.findByPickupLocation(pickupLocation);

        if (rides.empty())
            return result;

        rides.findFirst();
        while (true) {
            IRide ride = rides.retrieve();
            if (ride instanceof ISharedRide) {
                LinkedList<IRider> people = ((ISharedRide) ride).getParticipants();
                if (!people.empty()) {
                    people.findFirst();
                    while (true) {
                        IRider rider = people.retrieve();
                        if (!containsRider(result, rider.getId()))
                            result.insert(rider);
                        if (people.last())
                            break;
                        people.findNext();
                    }
                }
            }
            if (rides.last())
                break;
            rides.findNext();
        }
        return result;
    }

    // Returns all rides sorted alphabetically by pickup location.
    public LinkedList<IRide> getAllRidesAlphabetically() {
        return rideList.getAllAlphabetically();
    }

    // -------- private helpers --------

    // drop-off must be strictly after pickup
    private boolean validTimes(IDateTime pickup, IDateTime dropoff) {
        return pickup != null && dropoff != null && pickup.compareTo(dropoff) < 0;
    }

    // two intervals overlap when each starts before the other ends
    private boolean overlaps(IDateTime start1, IDateTime end1,
                             IDateTime start2, IDateTime end2) {
        return start1.compareTo(end2) < 0 && start2.compareTo(end1) < 0;
    }

    // true if the new interval overlaps any ride in this history
    private boolean hasConflict(LinkedList<IRide> history, IDateTime pickup, IDateTime dropoff) {
        if (history.empty())
            return false;
        history.findFirst();
        while (true) {
            IRide r = history.retrieve();
            if (overlaps(r.getPickupTime(), r.getDropoffTime(), pickup, dropoff))
                return true;
            if (history.last())
                return false;
            history.findNext();
        }
    }

    // returns a copy of a ride history so we can safely delete while looping
    private LinkedList<IRide> copyOf(LinkedList<IRide> source) {
        LinkedList<IRide> copy = new LinkedList<IRide>();
        if (source.empty())
            return copy;
        source.findFirst();
        while (true) {
            copy.insert(source.retrieve());
            if (source.last())
                break;
            source.findNext();
        }
        return copy;
    }

    // removes the ride with this ID from a history if it is there
    private void removeFromHistory(LinkedList<IRide> history, int rideId) {
        if (history.empty())
            return;
        history.findFirst();
        while (true) {
            if (history.retrieve().getRideId() == rideId) {
                history.remove();
                return;
            }
            if (history.last())
                return;
            history.findNext();
        }
    }

    // removes a ride from the ride list and from every person's history
    private void deleteRide(IRide ride) {
        rideList.removeRideById(ride.getRideId());
        removeFromHistory(ride.getDriver().getRideHistory(), ride.getRideId());

        if (ride instanceof IPrivateRide) {
            IRider rider = ((IPrivateRide) ride).getRider();
            removeFromHistory(rider.getRideHistory(), ride.getRideId());
        } else if (ride instanceof ISharedRide) {
            LinkedList<IRider> people = ((ISharedRide) ride).getParticipants();
            if (!people.empty()) {
                people.findFirst();
                while (true) {
                    removeFromHistory(people.retrieve().getRideHistory(), ride.getRideId());
                    if (people.last())
                        break;
                    people.findNext();
                }
            }
        }
    }

    // true if the list already has a rider with this ID
    private boolean containsRider(LinkedList<IRider> list, int riderId) {
        if (list.empty())
            return false;
        list.findFirst();
        while (true) {
            if (list.retrieve().getId() == riderId)
                return true;
            if (list.last())
                return false;
            list.findNext();
        }
    }
}
