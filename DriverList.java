/**
 * Stores all drivers in a structure maintained in sorted order by driver ID.
 */
public class DriverList implements IDriverList {
   private LinkedList<IDriver> drivers;   // sorted by driver ID (ascending)
   
   // Creates an empty driver list
   public DriverList() {
      drivers = new LinkedList<IDriver>();
   }

   // Adds a driver in sorted order by ID. Returns false if null or the ID exists.
   public boolean add(IDriver driver){
      if (driver == null)
         return false;
         
       // Duplicate check: after this we know the ID is not in the list
      if (findById(driver.getId()) != null)
         return false;  
      
      if (drivers.empty()) {         // Empty list: the driver becomes the head
         drivers.insert(driver);
         return true;
      }  
      
      /* New ID is smaller than the head's ID, so it must become the new head.
         insert() only adds AFTER current, so we put the new driver in the head
         node (update) and then insert the old head right after it.*/
      drivers.findFirst();
      if (driver.getId() < drivers.retrieve().getId()) {
         IDriver oldHead = drivers.retrieve();
         drivers.update(driver);
         drivers.insert(oldHead);
         return true;
      }
      
      // Walk until the next node has a bigger ID, then step back one node.
      // If there is no bigger ID, current stays on the last node.
      while (!drivers.last()) {
         drivers.findNext();
         if (drivers.retrieve().getId() > driver.getId()) {
            drivers.findPrevious();
            break;
         }
      } 
      // Insert after current, which keeps the list sorted
      drivers.insert(driver);
      return true;
   }

   // Returns the driver with this ID, or null if not found.
   public IDriver findById(int driverId){
      if (drivers.empty())
         return null;
   
      drivers.findFirst();
      while (!drivers.last()) {
         int currentId = drivers.retrieve().getId();
      
         if (currentId == driverId)
            return drivers.retrieve();   
         if (currentId > driverId)
            return null;                 // early stop 
         drivers.findNext();
      }
   
   // Check the last node
      if (drivers.retrieve().getId() == driverId)
         return drivers.retrieve();
   
      return null;
   
   
   }

   // Returns all drivers whose full name matches exactly (empty list if none).
   public LinkedList<IDriver> findByName(String fullName){
      LinkedList<IDriver> result = new LinkedList<IDriver>();
      if (fullName == null || drivers.empty())
         return result; 
   
      drivers.findFirst();
      while (!drivers.last()) {
         IDriver d = drivers.retrieve();
         if (d.getName().equals(fullName))
            result.insert(d);            
         drivers.findNext();
      }
      
         // Check the last node
      IDriver lastDriver = drivers.retrieve();
      if (lastDriver.getName().equals(fullName))
         result.insert(lastDriver);            
   
      
      
      return result;
   
   }

   // Returns the driver with this plate, or null. Plates are unique, so the first match is the only one.
   public IDriver findByVehiclePlate(String vehiclePlate){
      if (vehiclePlate == null || drivers.empty())
         return null;
      drivers.findFirst();
      while (!drivers.last()) {
         IDriver d = drivers.retrieve();
         if (d.getVehiclePlate().equals(vehiclePlate))
            return d;
         drivers.findNext();
      }
   
   // Check the last node
      if (drivers.retrieve().getVehiclePlate().equals(vehiclePlate))
         return drivers.retrieve();
   
      return null;
   }

      // Returns all drivers with this vehicle type (empty list if none).  
      LinkedList<IDriver> result = new LinkedList<IDriver>();
   
      if (vehicleType == null || drivers.empty())
         return result;
   
      drivers.findFirst();
      while (!drivers.last()) {
         IDriver d = drivers.retrieve();
         if (d.getVehicleType() == vehicleType)
            result.insert(d);
         drivers.findNext();
      }
   
   // Check the last node
      IDriver lastDriver = drivers.retrieve();
      if (lastDriver.getVehicleType() == vehicleType)
         result.insert(lastDriver);
   
      return result;
   }

   // Returns a new list with all drivers in ID order. We return a copy sooutside code cannot change our internal list.
   public LinkedList<IDriver> getAll(){
      LinkedList<IDriver> newList = new LinkedList<IDriver>();
   
      if (drivers.empty())
         return newList;
   
      drivers.findFirst();
      while (!drivers.last()) {
         newList.insert(drivers.retrieve());
         drivers.findNext();
      }
         // Copy the last node
         newList.insert(drivers.retrieve());
   
      return newList;
      
   }

   // Removes the driver with this ID. Returns true if found and removed.
   public boolean removeById(int driverId){
      if (drivers.empty())
         return false;
   
      drivers.findFirst();
      while (!drivers.last()) {
         int currentId = drivers.retrieve().getId();
      
         if (currentId == driverId) {
            drivers.remove();
            return true;                 // at most one driver has this ID
         } 
         if (currentId > driverId)
            return false;                // early stop: sorted
      
         drivers.findNext();
      }
      
      // Check the last node
      if (drivers.retrieve().getId() == driverId) {
         drivers.remove();
         return true; 
      }
            
      return false;        
      
   }

   // Removes the driver with this plate. Returns true if found and removed.
   public boolean removeByVehiclePlate(String vehiclePlate){
      if (vehiclePlate == null || drivers.empty())
         return false;
   
      drivers.findFirst();
      while (!drivers.last()) {
      
         if (drivers.retrieve().getVehiclePlate().equals(vehiclePlate)) {
            drivers.remove();
            return true;                 // at most one driver has this plate
         }
         drivers.findNext();
      }
   
         // Check the last node
      if (drivers.retrieve().getVehiclePlate().equals(vehiclePlate)) {
         drivers.remove();
         return true;
      }
   
      return false; 
      }

   // Removes EVERY driver with this exact full name. Returns how many were removed. Names are not unique, so we scan the whole list.
   public int removeByName(String fullName){
    
      if (fullName == null || drivers.empty())
         return 0;
         
      int count = 0;
      drivers.findFirst();
      while (!drivers.last()) {
      
         if (drivers.retrieve().getName().equals(fullName)) {
            drivers.remove();                // current already moved forward
            count++;
         } else  
            drivers.findNext();
                                   
      }
      // Check the last node
      if (drivers.retrieve().getName().equals(fullName)) {
         drivers.remove();              
         count++;}
   
      
      return count;
   }

  // Removes EVERY driver with this vehicle type. Returns how many were removed.Same idea as removeByName.
   public int removeByVehicleType(VehicleType vehicleType){
   
      if (vehicleType == null || drivers.empty())
         return 0;
         
      int count = 0;
      drivers.findFirst();
      while (!drivers.last()) { 
         if (drivers.retrieve().getVehicleType() == vehicleType) {
            drivers.remove();                // current already moved forward
            count++;
         } else  {
            drivers.findNext();
         }
      }
      // Check the last node
      if (drivers.retrieve().getVehicleType() == vehicleType) {
         drivers.remove();              
         count++;}
   
      return count;
   }

   // Returns the number of drivers stored.
   public int size(){ 
      return drivers.size();
   }
   
   
}//end class DriverList 