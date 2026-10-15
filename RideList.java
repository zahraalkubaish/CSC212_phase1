public class RideList implements IRideList {
 
    private LinkedList<IRide> rides;   // sorted by pickup location (A to Z)
 
    public RideList() {
        rides = new LinkedList<IRide>();
    }
 
    // ---------------------------------------------------------------
    // addRide: inserts the ride in its alphabetical place.
    // Rides with the same pickup location keep the order they were added
    // (the new one goes after the existing ones).
    // Fails if the ride is null or a ride with the same ID is already stored.
    // Time: O(n) for the duplicate check + O(n) to find the place = O(n).
    // ---------------------------------------------------------------
    @Override
    public boolean addRide(IRide ride) {
        if (ride == null || containsId(ride.getRideId()))
            return false;
 
        // Case 1: empty list -> the ride becomes the only node
        if (rides.empty()) {
            rides.insert(ride);
            return true;
        }
 
        // Case 2: goes before the current head.
        // insert() can only add AFTER current, so we use the same trick as
        // RiderList: put the new ride in the head node, then insert the
        // old head right after it.
        rides.findFirst();
        if (ride.compareTo(rides.retrieve()) < 0) {
            IRide oldHead = rides.retrieve();
            rides.update(ride);
            rides.insert(oldHead);
            return true;
        }
 
        // Case 3: walk until the next ride comes after the new one,
        // then insert after current. If we reach the end, insert at the end.
        while (!rides.last()) {
            rides.findNext();
            if (ride.compareTo(rides.retrieve()) < 0) {
                rides.findPrevious();
                break;
            }
        }
        rides.insert(ride);
        return true;
    }
 
    // ---------------------------------------------------------------
    // removeRideById: removes the ride with this ID, if present.
    // The list is sorted by location, not by ID, so we may need to
    // check every ride.
    // Time: O(n).
    // ---------------------------------------------------------------
    @Override
    public boolean removeRideById(int rideId) {
        if (rides.empty())
            return false;
 
        rides.findFirst();
        while (true) {
            if (rides.retrieve().getRideId() == rideId) {
                rides.remove();
                return true;
            }
            if (rides.last())
                return false;
            rides.findNext();
        }
    }
 
    // ---------------------------------------------------------------
    // getAllAlphabetically: returns a NEW list with every ride.
    // The internal list is already sorted, so one walk is enough.
    // Time: O(n).
    // ---------------------------------------------------------------
    @Override
    public LinkedList<IRide> getAllAlphabetically() {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (rides.empty())
            return result;
 
        rides.findFirst();
        while (true) {
            result.insert(rides.retrieve());
            if (rides.last())
                break;
            rides.findNext();
        }
        return result;
    }
 
    // ---------------------------------------------------------------
    // findByPickupLocation: every ride whose pickup location matches exactly.
    // Because the list is sorted, matching rides sit next to each other,
    // and we can stop as soon as we pass the location alphabetically.
    // Time: O(n) worst case.
    // ---------------------------------------------------------------
    @Override
    public LinkedList<IRide> findByPickupLocation(String pickupLocation) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (pickupLocation == null || rides.empty())
            return result;
 
        String key = pickupLocation.trim();
        rides.findFirst();
        while (true) {
            String location = rides.retrieve().getPickupLocation();
            if (location.equals(key))
                result.insert(rides.retrieve());
            else if (location.compareTo(key) > 0)
                break;      // early stop: everything after this is later in the alphabet
            if (rides.last())
                break;
            rides.findNext();
        }
        return result;
    }
 
    // ---------------------------------------------------------------
    // findByRiderName: every ride that has a rider with this exact full name.
    // A private ride has one rider to check; a shared ride has k.
    // Time: O(n * k) where k = the largest number of participants in a
    // shared ride. Since every participant is one of the riders, this is
    // O(n * r) in the worst case (r = number of riders).
    // ---------------------------------------------------------------
    @Override
    public LinkedList<IRide> findByRiderName(String riderFullName) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (riderFullName == null || rides.empty())
            return result;
 
        String key = riderFullName.trim();
        rides.findFirst();
        while (true) {
            IRide ride = rides.retrieve();
            if (rideHasRiderNamed(ride, key))
                result.insert(ride);
            if (rides.last())
                break;
            rides.findNext();
        }
        return result;
    }
 
    // ---------------------------------------------------------------
    // size: number of rides stored.
    // Time: O(1).
    // ---------------------------------------------------------------
    @Override
    public int size() {
        return rides.size();
    }
 
    // ===== private helpers =====
 
    // true if a ride with this ID is already in the list. Time: O(n).
    private boolean containsId(int rideId) {
        if (rides.empty())
            return false;
 
        rides.findFirst();
        while (true) {
            if (rides.retrieve().getRideId() == rideId)
                return true;
            if (rides.last())
                return false;
            rides.findNext();
        }
    }
 
    // true if this ride has a rider whose full name equals name.
    // Time: O(1) for a private ride, O(k) for a shared ride.
    private boolean rideHasRiderNamed(IRide ride, String name) {
        if (ride instanceof IPrivateRide)
            return ((IPrivateRide) ride).getRider().getName().equals(name);
 
        if (ride instanceof ISharedRide) {
            LinkedList<IRider> people = ((ISharedRide) ride).getParticipants();
            if (people.empty())
                return false;
            people.findFirst();
            while (true) {
                if (people.retrieve().getName().equals(name))
                    return true;
                if (people.last())
                    return false;
                people.findNext();
            }
        }
        return false;
    }
}
