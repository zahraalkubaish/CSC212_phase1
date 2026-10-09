/**
 * Abstract base class holding the fields shared by Rider and Driver.
 */
public abstract class Person implements IPerson {
    private final int id;                    // unique ID, never changes
    private String name;                     // full name
    private String phoneNumber;              // exactly 10 digits
    private LinkedList<IRide> rideHistory;   // rides of this person

    public Person(int id, String name, String phoneNumber) {
        this.id = id;
        setName(name);
        setPhoneNumber(phoneNumber);         // validates the format
        rideHistory = new LinkedList<IRide>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // The name must not be null or blank
    public void setName(String name) {
        if (name == null || name.trim().length() == 0)
            throw new IllegalArgumentException("Name must not be empty");
        this.name = name.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // The phone number must be exactly 10 digits (numeric characters only)
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 10)
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        for (int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            if (c < '0' || c > '9')
                throw new IllegalArgumentException("Phone number must contain digits only");
        }
        this.phoneNumber = phoneNumber;
    }

    public LinkedList<IRide> getRideHistory() {
        return rideHistory;
    }
}
