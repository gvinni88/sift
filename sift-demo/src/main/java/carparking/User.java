package carparking;

import carparking.annotations.Entity;

@Entity
class ParkingFloor {
    private final String id;
    private final int level;
    private final int availableSpaces;
    private final int occupiedSpaces;

    public ParkingFloor(String id, int level, int availableSpaces, int occupiedSpaces) {
        this.id = id;
        this.level = level;
        this.availableSpaces = availableSpaces;
        this.occupiedSpaces = occupiedSpaces;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getAvailableSpaces() {
        return availableSpaces;
    }

    public int getOccupiedSpaces() {
        return occupiedSpaces;
    }
}
