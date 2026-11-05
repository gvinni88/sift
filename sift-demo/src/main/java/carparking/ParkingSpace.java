package carparking;

import carparking.annotations.Entity;

/**
 * Represents a single parking slot in a multi-level car park.
 *
 * <p>Each space knows its floor, type, accessibility posture and current occupied state. This
 * class is intentionally small and acts as the domain record used by availability calculations.</p>
 *
 * @implSpec Occupancy is mutable because sensors or gate controls update the status at runtime.
 */
@Entity
public class ParkingSpace {
    private final String id;
    private final int floorNumber;
    private final String spaceType;
    private final boolean accessible;
    private boolean occupied;

    public ParkingSpace(String id, int floorNumber, String spaceType, boolean accessible, boolean occupied) {
        this.id = id;
        this.floorNumber = floorNumber;
        this.spaceType = spaceType;
        this.accessible = accessible;
        this.occupied = occupied;
    }

    public String getId() {
        return id;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
