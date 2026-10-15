public class PrivateRide extends Ride implements IPrivateRide {
 
    private IRider rider;   // the one rider of this ride
 
    // Creates a private ride. The parent constructor checks the shared fields.
    // Throws IllegalArgumentException if the rider is missing.
    // Time: O(1).
    public PrivateRide(String pickupLocation, IDateTime pickupTime,
                       IDateTime dropoffTime, String dropoffLocation,
                       IDriver driver, IRider rider) {
        super(pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
        setRider(rider);
    }
 
    // Time: O(1).
    @Override
    public IRider getRider() {
        return rider;
    }
 
    // Time: O(1).
    @Override
    public void setRider(IRider rider) {
        if (rider == null)
            throw new IllegalArgumentException("A private ride must have a rider");
        this.rider = rider;
    }
 
    // Only one rider to check: compare its ID.
    // Time: O(1).
    @Override
    public boolean hasRider(int riderId) {
        return rider.getId() == riderId;
    }
 
    // Time: O(1).
    @Override
    public String toString() {
        return "PrivateRide[" + commonDescription()
             + ", Rider=" + rider.getName() + " (" + rider.getId() + ")]";
    }
}
