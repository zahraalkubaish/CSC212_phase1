public abstract class Ride implements IRide {
 
    // Shared by ALL rides (static): the next ID to hand out.
    // Every new ride takes this value, then it goes up by one,
    // so no two rides ever get the same ID.
    private static int nextId = 1;
 
    // ===== Fields =====
    private final int rideId;               // unique, never changes
    private String pickupLocation;
    private String dropoffLocation;
    private final IDateTime pickupTime;     // set in the constructor only
    private final IDateTime dropoffTime;    // set in the constructor only
    private IDriver driver;
 
    // ===== Constructor =====
    // Validates the input and gives the ride a new unique ID.
    // Throws IllegalArgumentException if anything is missing, or if
    // the drop-off time is not after the pickup time.
    // Time: O(1).
    public Ride(String pickupLocation, IDateTime pickupTime,
                IDateTime dropoffTime, String dropoffLocation, IDriver driver) {
        if (pickupTime == null || dropoffTime == null)
            throw new IllegalArgumentException("Pickup and drop-off times are required");
        if (pickupTime.compareTo(dropoffTime) >= 0)
            throw new IllegalArgumentException("Drop-off time must be after pickup time");
 
        setPickupLocation(pickupLocation);
        setDropoffLocation(dropoffLocation);
        setDriver(driver);
        this.pickupTime = pickupTime;
        this.dropoffTime = dropoffTime;
 
        this.rideId = nextId;
        nextId++;
    }
 
    // ===== Getters and setters =====  (all O(1))
 
    @Override
    public int getRideId() {
        return rideId;
    }
 
    @Override
    public String getPickupLocation() {
        return pickupLocation;
    }
 
    @Override
    public void setPickupLocation(String pickupLocation) {
        if (pickupLocation == null || pickupLocation.trim().length() == 0)
            throw new IllegalArgumentException("Pickup location must not be empty");
        this.pickupLocation = pickupLocation.trim();
    }
 
    @Override
    public IDateTime getPickupTime() {
        return pickupTime;
    }
 
    @Override
    public IDateTime getDropoffTime() {
        return dropoffTime;
    }
 
    @Override
    public String getDropoffLocation() {
        return dropoffLocation;
    }
 
    @Override
    public void setDropoffLocation(String dropoffLocation) {
        if (dropoffLocation == null || dropoffLocation.trim().length() == 0)
            throw new IllegalArgumentException("Drop-off location must not be empty");
        this.dropoffLocation = dropoffLocation.trim();
    }
 
    @Override
    public IDriver getDriver() {
        return driver;
    }
 
    @Override
    public void setDriver(IDriver driver) {
        if (driver == null)
            throw new IllegalArgumentException("A ride must have a driver");
        this.driver = driver;
    }
 
    // ===== compareTo =====
    // Orders rides alphabetically by pickup location (uses String's compareTo).
    // RideList relies on this to keep its rides in order.
    // Time: O(1) (location names are short).
    @Override
    public int compareTo(IRide other) {
        return this.pickupLocation.compareTo(other.getPickupLocation());
    }
 
    // ===== hasRider =====
    // Left abstract: a private ride checks its one rider,
    // a shared ride walks its list of participants.
    @Override
    public abstract boolean hasRider(int riderId);
 
    // ===== toString helper =====
    // The common part of the description; each child adds its riders.
    // Example: "ID=3, RiyadhSouth (10/28/2026 08:45) -> RiyadhEast (10/28/2026 10:15), Driver=Talal Al-Farhan (4)"
    // Time: O(1).
    protected String commonDescription() {
        return "ID=" + rideId
             + ", " + pickupLocation + " (" + pickupTime.format() + ")"
             + " -> " + dropoffLocation + " (" + dropoffTime.format() + ")"
             + ", Driver=" + driver.getName() + " (" + driver.getId() + ")";
    }
}
