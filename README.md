# Ride-Sharing System (CSC 212 - Phase 1)

A command-line ride-sharing application written in Java for the CSC 212 Data Structures
course at King Saud University (Fall 2026). It manages riders, drivers, private rides and
shared (carpool) rides using **our own doubly linked list**, with no `java.util` collections.

## Features
- Add, search and remove riders and drivers (by ID, name, email/plate, city/vehicle type)
- Schedule private and shared rides with conflict detection for riders and drivers
- Cascade removal: removing a rider or driver also fixes or deletes their rides
- Riders, drivers and rides loaded automatically from CSV files at startup
- Numbered command-line menu (options 1-12, 0 to exit)

## Project structure
| Part | Files |
|---|---|
| Data structure | `Node`, `List`, `LinkedList` |
| People | `Person`, `Rider`, `RiderList`, `Driver`, `DriverList`, `VehicleType` |
| Rides | `DateTime`, `Ride`, `PrivateRide`, `SharedRide`, `RideList` |
| System and menu | `RideSharingSystem`, `Main` |
| Interfaces (given, not modified) | `IPerson`, `IRider`, `IRiderList`, `IDriver`, `IDriverList`, `IDateTime`, `IRide`, `IPrivateRide`, `ISharedRide`, `IRideList`, `IRideSharingSystem` |
| Data | `riders_100.csv`, `drivers_30.csv`, `rides_40.csv` |

## How to run
```
javac *.java
java Main
```
Run it from the folder that contains the three CSV files.

## Team
| Member | Responsibility |
|---|---|
| A | LinkedList, Person, Rider, RiderList, rider operations |
| B | Driver, DriverList, VehicleType, driver operations, Main structure |
| C | DateTime, Ride types, RideList, scheduling |

## Rules we follow
- Interfaces and method signatures are never modified
- Only `Main` reads input or prints output
- No `java.util` collections
