/**
 * Linked list of rides kept in alphabetical order by pickup location.
 */
public class RideList implements IRideList {
    private LinkedList<IRide> rides;   // sorted by pickup location

    public RideList() {
        rides = new LinkedList<IRide>();
    }

    // Inserts the ride in alphabetical order; fails for null or a repeated ride ID
    public boolean addRide(IRide ride) {
        if (ride == null || containsRideId(ride.getRideId()))
            return false;
        if (rides.empty()) {
            rides.insert(ride);
            return true;
        }
        rides.findFirst();
        while (true) {
            if (rides.retrieve().compareTo(ride) > 0) {
                IRide later = rides.retrieve();
                rides.update(ride);       // the new ride takes this place
                rides.insert(later);      // the later ride is pushed right after it
                return true;
            }
            if (rides.last()) {           // every ride comes before: append at the end
                rides.insert(ride);
                return true;
            }
            rides.findNext();
        }
    }

    // Removes the ride with this ID; returns false if it does not exist
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

    // Returns a new list with every ride (already alphabetical): O(n)
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

    public LinkedList<IRide> findByPickupLocation(String pickupLocation) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (pickupLocation == null || rides.empty())
            return result;
        rides.findFirst();
        while (true) {
            IRide r = rides.retrieve();
            if (r.getPickupLocation().equals(pickupLocation))
                result.insert(r);
            if (rides.last())
                break;
            rides.findNext();
        }
        return result;
    }

    // Returns every ride (private or shared) that involves a rider with this full name
    public LinkedList<IRide> findByRiderName(String riderFullName) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (riderFullName == null || rides.empty())
            return result;
        rides.findFirst();
        while (true) {
            IRide r = rides.retrieve();
            if (involvesRiderNamed(r, riderFullName))
                result.insert(r);
            if (rides.last())
                break;
            rides.findNext();
        }
        return result;
    }

    public int size() {
        return rides.size();
    }

    // True if some ride in the list already has this ride ID
    private boolean containsRideId(int rideId) {
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

    // Checks the rider of a private ride, or every participant of a shared ride
    private boolean involvesRiderNamed(IRide ride, String fullName) {
        if (ride instanceof IPrivateRide)
            return ((IPrivateRide) ride).getRider().getName().equals(fullName);
        if (ride instanceof ISharedRide) {
            LinkedList<IRider> people = ((ISharedRide) ride).getParticipants();
            if (people.empty())
                return false;
            people.findFirst();
            while (true) {
                if (people.retrieve().getName().equals(fullName))
                    return true;
                if (people.last())
                    return false;
                people.findNext();
            }
        }
        return false;
    }
}
