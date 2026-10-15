public class SharedRide extends Ride implements ISharedRide {
 
    private LinkedList<IRider> participants;   // riders on this ride, in the order they joined
 
    // Creates a shared ride with no participants yet.
    // Riders are added afterwards with addParticipant.
    // Time: O(1).
    public SharedRide(String pickupLocation, IDateTime pickupTime,
                      IDateTime dropoffTime, String dropoffLocation, IDriver driver) {
        super(pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
        participants = new LinkedList<IRider>();
    }
 
    // Returns a NEW list with the participants, so code outside this class
    // cannot change our internal list by accident.
    // Time: O(k).
    @Override
    public LinkedList<IRider> getParticipants() {
        LinkedList<IRider> copy = new LinkedList<IRider>();
        if (participants.empty())
            return copy;
 
        participants.findFirst();
        while (true) {
            copy.insert(participants.retrieve());
            if (participants.last())
                break;
            participants.findNext();
        }
        return copy;
    }
 
    // Adds a rider at the end of the list.
    // Fails (returns false) if the rider is null or already on this ride.
    // Time: O(k) for the duplicate check + O(k) to reach the end = O(k).
    @Override
    public boolean addParticipant(IRider rider) {
        if (rider == null || hasRider(rider.getId()))
            return false;
 
        // insert() adds AFTER current, so move current to the last node first
        if (!participants.empty()) {
            participants.findFirst();
            participants.findLast();
        }
        participants.insert(rider);
        return true;
    }
 
    // Removes the rider with this ID, if present.
    // Time: O(k).
    @Override
    public boolean removeParticipantById(int riderId) {
        if (participants.empty())
            return false;
 
        participants.findFirst();
        while (true) {
            if (participants.retrieve().getId() == riderId) {
                participants.remove();
                return true;
            }
            if (participants.last())
                return false;
            participants.findNext();
        }
    }
 
    // Time: O(1).
    @Override
    public boolean isEmpty() {
        return participants.empty();
    }
 
    // Walks the list and compares each participant's ID.
    // Returns false only after checking the whole list.
    // Time: O(k).
    @Override
    public boolean hasRider(int riderId) {
        if (participants.empty())
            return false;
 
        participants.findFirst();
        while (true) {
            if (participants.retrieve().getId() == riderId)
                return true;
            if (participants.last())
                return false;
            participants.findNext();
        }
    }
 
    // Time: O(k).
    @Override
    public String toString() {
        String names = "";
        if (!participants.empty()) {
            participants.findFirst();
            while (true) {
                IRider r = participants.retrieve();
                names += r.getName() + " (" + r.getId() + ")";
                if (participants.last())
                    break;
                names += ", ";
                participants.findNext();
            }
        }
        return "SharedRide[" + commonDescription() + ", Riders={" + names + "}]";
    }
}
