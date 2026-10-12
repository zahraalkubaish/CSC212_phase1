/**
 * RiderList: stores riders in a linked list kept in sorted order by rider ID.
 * Uses composition over LinkedList<IRider>.
 */
public class RiderList implements IRiderList {

    // The internal linked list. Sorted by rider ID (ascending).
    private LinkedList<IRider> riders;

    public RiderList() {
        riders = new LinkedList<IRider>();
    }

    // ---------------------------------------------------------------
    // add: inserts in sorted order by ID. Rejects duplicates.
    // Uses findById for the duplicate check, then walks to the
    // insertion point (no inline duplicate checks needed).
    // Time: O(n). 
    // ---------------------------------------------------------------
    public boolean add(IRider rider) {

        if (rider == null) {
            return false;
        }

        // Duplicate check: O(n). After this, we KNOW no duplicate exists.
        if (findById(rider.getId()) != null) {
            return false;
        }

        // Empty list: rider becomes the head.
        if (riders.empty()) {
            riders.insert(rider);
            return true;
        }

        riders.findFirst();
        int headId = riders.retrieve().getId();

        // Insert at head via update + insert trick:
        //   update() replaces head's data with the new rider,
        //   insert() puts the old head right after it.
        if (rider.getId() < headId) {
            IRider oldHead = riders.retrieve();
            riders.update(rider);        // head now holds the new rider
            riders.insert(oldHead);      // old head goes right after it
            return true;
        }

        // Walk to the insertion point. Since we already ruled out duplicates,
        // we only need to find the first node with id > rider.id.
        while (!riders.last()) {
            riders.findNext();
            int nextId = riders.retrieve().getId();
            if (nextId > rider.getId()) {
                riders.findPrevious();
                break;
            }
        }

        riders.insert(rider);
        return true;
    }

    // ---------------------------------------------------------------
    // size: returns number of stored riders. O(1).
    // ---------------------------------------------------------------
    public int size() {
        return riders.size();
    }

    // ---------------------------------------------------------------
    // findById: returns the rider with the given ID, or null.
    // Exploits sorted order for early stop. Time: O(n) worst.
    // ---------------------------------------------------------------
    public IRider findById(int riderId) {
        if (riders.empty())
            return null;

        riders.findFirst();

        while (!riders.last()) {
            int currentId = riders.retrieve().getId();
            if (currentId == riderId)
                return riders.retrieve();
            if (currentId > riderId)
                return null;   // early stop: sorted
            riders.findNext();
        }

        // Check the last element (loop stops before examining it)
        if (riders.retrieve().getId() == riderId)
            return riders.retrieve();

        return null;
    }

    // ---------------------------------------------------------------
    // findByEmail: returns the rider with the given email, or null.
    // Not sorted by email -> must scan whole list. Time: O(n).
    // ---------------------------------------------------------------
    public IRider findByEmail(String email) {
        if (email == null || riders.empty())
            return null;

        riders.findFirst();

        while (!riders.last()) {
            IRider currentRider = riders.retrieve();
            if (currentRider.getEmail().equals(email))
                return currentRider;
            riders.findNext();
        }

        if (riders.retrieve().getEmail().equals(email))
            return riders.retrieve();

        return null;
    }

    // ---------------------------------------------------------------
    // findByName: all riders whose full name matches exactly.
    // Time: O(n). 
    // ---------------------------------------------------------------
    public LinkedList<IRider> findByName(String fullName) {
        return findAll(fullName, true);
    }

    // ---------------------------------------------------------------
    // findByHomeCity: all riders from the given home city.
    // Time: O(n). 
    // ---------------------------------------------------------------
    public LinkedList<IRider> findByHomeCity(String homeCity) {
        return findAll(homeCity, false);
    }

    // ---------------------------------------------------------------
    // findAll (private helper): collects every rider whose name
    // (byName = true) or home city (byName = false) equals key.
    // Time: O(n). 
    // ---------------------------------------------------------------
    private LinkedList<IRider> findAll(String key, boolean byName) {
        LinkedList<IRider> result = new LinkedList<IRider>();

        if (key == null || riders.empty())
            return result;

        riders.findFirst();
        while (true) {
            IRider r = riders.retrieve();
            String value = byName ? r.getName() : r.getHomeCity();
            if (value.equals(key))
                result.insert(r);
            if (riders.last())
                break;
            riders.findNext();
        }
        return result;
    }

    // ---------------------------------------------------------------
    // getAll: returns a NEW list with all riders (same order).
    // Does not expose the internal list. Time: O(n). 
    // ---------------------------------------------------------------
    public LinkedList<IRider> getAll() {
        LinkedList<IRider> result = new LinkedList<IRider>();

        if (riders.empty())
            return result;

        riders.findFirst();
        while (true) {
            result.insert(riders.retrieve());
            if (riders.last())
                break;
            riders.findNext();
        }
        return result;
    }

    // ---------------------------------------------------------------
    // removeById: removes the rider with the given ID.
    // Time: O(n) worst. 
    // ---------------------------------------------------------------
    public boolean removeById(int riderId) {
        if (riders.empty())
            return false;

        riders.findFirst();
        while (true) {
            if (riders.retrieve().getId() == riderId) {
                riders.remove();
                return true;
            }
            if (riders.last())
                return false;
            riders.findNext();
        }
    }

    // ---------------------------------------------------------------
    // removeByEmail: removes the rider with the given email.
    // Time: O(n) worst. 
    // ---------------------------------------------------------------
    public boolean removeByEmail(String email) {
        if (email == null || riders.empty())
            return false;

        riders.findFirst();
        while (true) {
            if (riders.retrieve().getEmail().equals(email)) {
                riders.remove();
                return true;
            }
            if (riders.last())
                return false;
            riders.findNext();
        }
    }

    // ---------------------------------------------------------------
    // removeByName: removes EVERY rider with this exact full name.
    // Returns count removed. Time: O(n). 
    // ---------------------------------------------------------------
    public int removeByName(String fullName) {
        return removeAll(fullName, true);
    }

    // ---------------------------------------------------------------
    // removeByHomeCity: removes EVERY rider from this home city.
    // Returns count removed. Time: O(n). 
    // ---------------------------------------------------------------
    public int removeByHomeCity(String homeCity) {
        return removeAll(homeCity, false);
    }

    // ---------------------------------------------------------------
    // removeAll (private helper).
    //
    // Key idea: remove() auto-advances current. So:
    //   - after removal, do NOT call findNext()
    //   - after a non-removal, we DO call findNext()
    //   - wasLast must be captured BEFORE any modification
    //
    // Time: O(n).
    // ---------------------------------------------------------------
    private int removeAll(String key, boolean byName) {
        int count = 0;

        if (key == null || riders.empty())
            return 0;

        riders.findFirst();
        while (true) {
            boolean wasLast = riders.last();   // read BEFORE remove()
            IRider r = riders.retrieve();
            String value = byName ? r.getName() : r.getHomeCity();

            if (value.equals(key)) {
                riders.remove();
                count++;
                if (wasLast || riders.empty())
                    break;
            } else {
                if (wasLast)
                    break;
                riders.findNext();
            }
        }
        return count;
    }
}
