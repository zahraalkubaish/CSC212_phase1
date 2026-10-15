public class Driver extends Person implements IDriver{

   private String vehiclePlate;          // 3 uppercase letters + 4 digits (e.g., ABC1234)
   private VehicleType vehicleType;     // SEDAN, LUXURY_SEDAN, SUV or VAN

   //Constructor
   public Driver(int id, String name, String phoneNumber, String vehiclePlate, VehicleType vehicleType) {
      super(id, name, phoneNumber); 
      setVehiclePlate(vehiclePlate);   // validates the plate format
      setVehicleType(vehicleType);    // validates that the type is not null
   }


   // Returns the vehicle plate number
   public String getVehiclePlate() {
      return vehiclePlate;
   }

   // Sets the plate. It must be exactly 3 uppercase letters followed by 4 digits.
   public void setVehiclePlate(String vehiclePlate) {
      if (vehiclePlate == null || vehiclePlate.length() != 7) {       // Total length must be 7
         throw new IllegalArgumentException("Invalid vehicle plate format.");
      }
   
      for (int i = 0; i < 3; i++) {
         char c = vehiclePlate.charAt(i);
         if (c < 'A' || c > 'Z') 
            throw new IllegalArgumentException("First 3 characters must be uppercase letters.");   
      }
   
      for (int i = 3; i < 7; i++) {
         char c = vehiclePlate.charAt(i);
         if (c < '0' || c > '9') 
            throw new IllegalArgumentException("Last 4 characters must be digits.");
        
      }
      this.vehiclePlate = vehiclePlate;
   }
 
   // Returns the vehicle type.
   public VehicleType getVehicleType() {
      return vehicleType;
   }


   // Sets the vehicle type. It must not be null.
   public void setVehicleType(VehicleType vehicleType) {
      if (vehicleType == null)
         throw new IllegalArgumentException("Vehicle type must not be null");
   
      this.vehicleType = vehicleType;
   }

   
   /*Compares driver IDs in ascending order
      Returns a negative value (-1) if this ID is smaller than the other ID.
      Returns zero (0) if both IDs are equal.
      Returns a positive value (1) if this ID is greater than the other ID*/
   @Override
   public int compareTo(IDriver other) {
      return Integer.compare(this.getId(), other.getId());
   }
   
   
   // Two drivers are equal when they have the same ID (consistent with compareTo).
   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof IDriver))         // Anything that is not a driver (or is null) cannot be equal
         return false;
      return getId() == ((IDriver) obj).getId();
   }
   
   
    // Uses the ID, so equal drivers always have the same hash code.
    @Override   
    public int hashCode() {
      return getId();
   }
 
    // Returns a readable description of the driver.
    @Override
    public String toString() {
      return "Driver[ID=" + getId() + ", Name=" + getName()
                + ", Phone=" + getPhoneNumber() + ", Plate=" + vehiclePlate
                + ", Vehicle=" + vehicleType + "]";
   }    
    
}//End class Driver