package carparking.features.accessibility;

import carparking.annotations.Entity;

@Entity
public class AccessibleParkingSpace {
    private final String id;
    private final String zoneCode;
    private boolean reserved;

    public AccessibleParkingSpace(String id, String zoneCode, boolean reserved) {
        this.id = id;
        this.zoneCode = zoneCode;
        this.reserved = reserved;
    }

    public String getId() {
        return id;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}
