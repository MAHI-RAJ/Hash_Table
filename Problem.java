
import java.util.*;

public class SmartParkingLot {
    enum Status { EMPTY, OCCUPIED, AVAILABLE }

    static class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status = Status.EMPTY;

        void park(String plate) {
            this.licensePlate = plate;
            this.entryTime = System.currentTimeMillis();
            this.status = Status.OCCUPIED;
        }

        void leave() {
            this.status = Status.AVAILABLE;
            this.licensePlate = null;
        }
    }

    private final ParkingSpot[] lot;
    private final int capacity;
    private int totalParked = 0;

    public SmartParkingLot(int size) {
        this.capacity = size;
        this.lot = new ParkingSpot[size];
        for (int i = 0; i < size; i++) lot[i] = new ParkingSpot();
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % capacity;
    }

    /**
     * Requirement: Assign spot using Linear Probing
     */
    public String parkVehicle(String plate) {
        if (totalParked >= capacity) return "Lot Full";

        int preferredSpot = hash(plate);
        int probes = 0;

        while (probes < capacity) {
            int currentSpot = (preferredSpot + probes) % capacity;

            // We can park in EMPTY or AVAILABLE (previously occupied) spots
            if (lot[currentSpot].status != Status.OCCUPIED) {
                lot[currentSpot].park(plate);
                totalParked++;
                return String.format("Vehicle %s assigned to Spot #%d (%d probes)",
                        plate, currentSpot, probes);
            }
            probes++;
        }
        return "System Error: Could not allocate spot";
    }

    /**
     * Requirement: Find vehicle and calculate fee
     */
    public String exitVehicle(String plate) {
        int preferredSpot = hash(plate);
        int probes = 0;

        while (probes < capacity) {
            int currentSpot = (preferredSpot + probes) % capacity;

            // If we hit an EMPTY spot, the vehicle was never here
            if (lot[currentSpot].status == Status.EMPTY) break;

            if (lot[currentSpot].status == Status.OCCUPIED &&
                    plate.equals(lot[currentSpot].licensePlate)) {

                long duration = System.currentTimeMillis() - lot[currentSpot].entryTime;
                lot[currentSpot].leave();
                totalParked--;
                return String.format("Vehicle %s exited. Spot #%d freed. Fee: $%.2f",
                        plate, currentSpot, (duration / 1000.0) * 0.50);
            }
            probes++;
        }
        return "Vehicle not found.";

    }
}