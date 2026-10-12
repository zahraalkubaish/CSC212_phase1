import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Coordinates the rider, driver and ride lists and enforces the system rules:
 * unique IDs/plates, existence checks, conflict-free schedules and cascade removal.
 * This class never prints: every search returns its result.
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

    // ---------------------------------------------------------------- CSV loading

    // Line format: riderId,name,email,phoneNumber,homeCity. Invalid lines are skipped.
    public boolean loadRidersFromCSV(String ridersFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ridersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.trim().split(",", -1);
                if (f.length != 5)
                    continue;
                try {
                    addRider(new Rider(Integer.parseInt(f[0].trim()), f[1], f[2], f[3].trim(), f[4]));
                } catch (IllegalArgumentException e) {
                    // bad or header line: skip it and keep loading
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Line format: driverId,name,phoneNumber,vehiclePlate,vehicleType. Invalid lines are skipped.
    public boolean loadDriversFromCSV(String driversFilePath) {
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
                    addDriver(new Driver(Integer.parseInt(f[0].trim()), f[1], f[2].trim(), f[3].trim(), type));
                } catch (IllegalArgumentException e) {
                    // bad or header line: skip it and keep loading
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Line format: rideType,pickup,pickupDateTime,dropoffDateTime,dropoff,driverId,riderIds
    // Riders and drivers must already be loaded. Rides that break a rule are skipped.
    public boolean loadRidesFromCSV(String ridesFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ridesFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.trim().split(",", -1);
                if (f.length != 7)
                    continue;
                try {
                    String type = f[0].trim().toUpperCase();
                    DateTime pickup = DateTime.parse(f[2]);
                    DateTime dropoff = DateTime.parse(f[3]);
                    int driverId = Integer.parseInt(f[5].trim());
                    String[] idTexts = f[6].trim().split(";");
                    int[] riderIds = new int[idTexts.length];
                    for (int i = 0; i < idTexts.length; i++)
                        riderIds[i] = Integer.parseInt(idTexts[i].trim());
                    if (type.equals("PRIVATE") && riderIds.length == 1)
                        schedulePrivateRide(f[1], pickup, dropoff, f[4], riderIds[0], driverId);
                    else if (type.equals("SHARED"))
                        scheduleSharedRide(f[1], pickup, dropoff, f[4], riderIds, driverId);
                } catch (IllegalArgumentException e) {
                    // bad or header line: skip it and keep loading
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Converts text such as "LUXURY SEDAN" or "luxury_sedan" to the enum; null if unknown
    public static VehicleType parseVehicleType(String text) {
        if (text == null)
            return null;
        String normalized = text.trim().toUpperCase().replace(' ', '_').replace('-', '_');
        try {
            return VehicleType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ---------------------------------------------------------------- riders

    public boolean addRider(IRider rider) {
        if (rider == null)
            return false;
        return riderList.add(rider);
    }

    public IRider searchRiderById(int riderId) {
        return riderList.findById(riderId);
    }

    public IRider searchRiderByEmail(String email) {
        return riderList.findByEmail(email);
    }

    public LinkedList<IRider> searchRidersByName(String fullName) {
        return riderList.findByName(fullName);
    }

    public LinkedList<IRider> searchRidersByHomeCity(String homeCity) {
        return riderList.findByHomeCity(homeCity);
    }

    public LinkedList<IRider> getAllRiders() {
        return riderList.getAll();
    }

    // ---------------------------------------------------------------- drivers

    // Fails on a duplicate ID or a duplicate vehicle plate
    public boolean addDriver(IDriver driver) {
        if (driver == null)
            return false;
        if (driverList.findByVehiclePlate(driver.getVehiclePlate()) != null)
            return false;
        return driverList.add(driver);
    }

    public IDriver searchDriverById(int driverId) {
        return driverList.findById(driverId);
    }

    public IDriver searchDriverByVehiclePlate(String vehiclePlate) {
        return driverList.findByVehiclePlate(vehiclePlate);
    }

    public LinkedList<IDriver> searchDriversByVehicleType(VehicleType vehicleType) {
        return driverList.findByVehicleType(vehicleType);
    }

    public LinkedList<IDriver> getAllDrivers() {
        return driverList.getAll();
    }

    // ---------------------------------------------------------------- cascade removal

    // Deletes the rider's private rides, removes him/her from shared rides,
    // and deletes shared rides left with no riders.
    public boolean removeRider(int riderId) {
        IRider rider = riderList.findById(riderId);
        if (rider == null)
            return false;
        LinkedList<IRide> rides = copyOf(rider.getRideHistory());   // safe copy to loop over
        if (!rides.empty()) {
            rides.findFirst();
            while (true) {
                IRide ride = rides.retrieve();
                if (ride instanceof ISharedRide) {
                    ISharedRide shared = (ISharedRide) ride;
                    shared.removeParticipantById(riderId);
                    removeFromHistory(rider.getRideHistory(), ride.getRideId());
                    if (shared.isEmpty())
                        deleteRide(ride);
                } else {
                    deleteRide(ride);
                }
                if (rides.last())
                    break;
                rides.findNext();
            }
        }
        return riderList.removeById(riderId);
    }

    // Deletes every ride (private or shared) assigned to the driver, then the driver
    public boolean removeDriver(int driverId) {
        IDriver driver = driverList.findById(driverId);
        if (driver == null)
            return false;
        LinkedList<IRide> rides = copyOf(driver.getRideHistory());  // safe copy to loop over
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

    // ---------------------------------------------------------------- scheduling

    // Requires an existing rider and driver, and no overlap with either schedule
    public boolean schedulePrivateRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                                       String dropoffLocation, int riderId, int driverId) {
        IRider rider = riderList.findById(riderId);
        IDriver driver = driverList.findById(driverId);
        if (rider == null || driver == null || !validTimes(pickupTime, dropoffTime))
            return false;
        if (hasConflict(rider.getRideHistory(), pickupTime, dropoffTime)
                || hasConflict(driver.getRideHistory(), pickupTime, dropoffTime))
            return false;
        PrivateRide ride;
        try {
            ride = new PrivateRide(pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver, rider);
        } catch (IllegalArgumentException e) {
            return false;
        }
        rideList.addRide(ride);
        rider.getRideHistory().insert(ride);
        driver.getRideHistory().insert(ride);
        return true;
    }

    // Requires existing riders (no repeats) and driver, and no overlap with any schedule
    public boolean scheduleSharedRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                                      String dropoffLocation, int[] riderIds, int driverId) {
        if (riderIds == null || riderIds.length == 0 || !validTimes(pickupTime, dropoffTime))
            return false;
        IDriver driver = driverList.findById(driverId);
        if (driver == null || hasConflict(driver.getRideHistory(), pickupTime, dropoffTime))
            return false;
        // check every rider before creating anything
        for (int i = 0; i < riderIds.length; i++) {
            for (int j = 0; j < i; j++) {
                if (riderIds[i] == riderIds[j])
                    return false;                       // the same rider listed twice
            }
            IRider rider = riderList.findById(riderIds[i]);
            if (rider == null || hasConflict(rider.getRideHistory(), pickupTime, dropoffTime))
                return false;
        }
        SharedRide ride;
        try {
            ride = new SharedRide(pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
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

    // ---------------------------------------------------------------- ride searches

    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation) {
        return rideList.findByPickupLocation(pickupLocation);
    }

    public LinkedList<IRide> searchRidesByRiderName(String riderName) {
        return rideList.findByRiderName(riderName);
    }

    // Riders of the shared ride(s) starting at this pickup location (no repeated riders)
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

    public LinkedList<IRide> getAllRidesAlphabetically() {
        return rideList.getAllAlphabetically();
    }

    // ---------------------------------------------------------------- private helpers

    // The drop-off must be strictly after the pickup
    private boolean validTimes(IDateTime pickup, IDateTime dropoff) {
        return pickup != null && dropoff != null && pickup.compareTo(dropoff) < 0;
    }

    // Two intervals overlap exactly when start1 < end2 and start2 < end1
    private boolean overlaps(IDateTime start1, IDateTime end1, IDateTime start2, IDateTime end2) {
        return start1.compareTo(end2) < 0 && start2.compareTo(end1) < 0;
    }

    // True if the new interval overlaps any ride in this person's history
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

    // Returns a new list with the same rides, so the original can be changed while looping
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

    // Removes the ride with this ID from a ride history, if present
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

    // Removes a ride from the ride list and from the history of its driver and riders
    private void deleteRide(IRide ride) {
        rideList.removeRideById(ride.getRideId());
        removeFromHistory(ride.getDriver().getRideHistory(), ride.getRideId());
        if (ride instanceof IPrivateRide) {
            removeFromHistory(((IPrivateRide) ride).getRider().getRideHistory(), ride.getRideId());
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

    // True if the list already holds a rider with this ID
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
