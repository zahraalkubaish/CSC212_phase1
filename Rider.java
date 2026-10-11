/**
 * A rider of the ride-sharing system. Riders are compared and identified by ID.
 */
public class Rider extends Person implements IRider {
    private String email;      // rider's email address
    private String homeCity;   // rider's home city

    public Rider(int id, String name, String email, String phoneNumber, String homeCity) {
        super(id, name, phoneNumber);
        setEmail(email);
        setHomeCity(homeCity);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().length() == 0)
            throw new IllegalArgumentException("Email must not be empty");
        this.email = email.trim();
    }

    public String getHomeCity() {
        return homeCity;
    }

    public void setHomeCity(String homeCity) {
        if (homeCity == null || homeCity.trim().length() == 0)
            throw new IllegalArgumentException("Home city must not be empty");
        this.homeCity = homeCity.trim();
    }

    // Orders riders strictly by ID
    @Override
    public int compareTo(IRider other) {
        if (getId() < other.getId())
            return -1;
        if (getId() > other.getId())
            return 1;
        return 0;
    }

    // Two riders are equal when they have the same ID (consistent with compareTo)
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IRider))
            return false;
        return getId() == ((IRider) obj).getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        return "Rider[ID=" + getId() + ", Name=" + getName() + ", Email=" + email
                + ", Phone=" + getPhoneNumber() + ", City=" + homeCity + "]";
    }
}
